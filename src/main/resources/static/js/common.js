(function($) {

    // --------------------------------------
    // base
    // --------------------------------------

    $.extend({
        namespace2fn: function(name, fun) {
            if (name) {
                $.fn[name] = fun ? fun : function() {
                    arguments.callee.$ = this;
                    return arguments.callee;
                };
            }
            return this;
        },
        namespace2win: function() {
            var a = arguments,
                o = null,
                i, j, d;
            for (i = 0; i < a.length; i = i + 1) {
                d = a[i].split(".");
                o = window;
                for (j = (d[0] == "window") ? 1 : 0; j < d.length; j = j + 1) {
                    o[d[j]] = o[d[j]] || {};
                    o = o[d[j]];
                }
            }
            return o;
        },
        getWindowFunction: function(name) {
            // 
            if (window[name])
                return window[name];
            if (window.parent[name])
                return window.parent[name];

            // 只检查一层
            var frames = window.frames;
            var i;

            for (i = 0; i < frames.length; i++) {
                var fun = frames[i][name];
                if (fun)
                    return fun;
            }
        },
        formatDate: function(date, format) {

            var o = {
                "M+": date.getMonth() + 1, //month
                "d+": date.getDate(), //day
                "h+": date.getHours(), //hour
                "m+": date.getMinutes(), //minute
                "s+": date.getSeconds(), //second
                "q+": Math.floor((date.getMonth() + 3) / 3), //quarter
                "S": date.getMilliseconds() //millisecond
            }
            if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
                (date.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (var k in o)
                if (new RegExp("(" + k + ")").test(format))
                    format = format.replace(RegExp.$1,
                        RegExp.$1.length == 1 ? o[k] :
                        ("00" + o[k]).substr(("" + o[k]).length));
            return format;
        },
        getUrlVariable: function getQueryVariable(variable) {
            var query = window.location.search.substring(1);
            var vars = query.split("&");
            for (var i = 0; i < vars.length; i++) {
                var pair = vars[i].split("=");
                if (pair[0] == variable) { return pair[1]; }
            }
            return false;
        }
    });

    // --------------------------------------
    // constant
    // --------------------------------------
    _RESPONSE_STATUS = {
        NO_LOGIN: -1,
        NO_PERMISSION: -2,
        SUCCESS: 1,
        FAIL: 2,
        FAIL_VALID: 3,
        ERROR: 0
    };

    $.namespace2win('tonto.constant');

    // --------------------------------------
    // messager
    // --------------------------------------

    /*
     * options 参数配置基于Messager，参考 http://github.hubspot.com/messenger/
     * 
     */

    $.extend({
        infoMessage: function(message) {
            layer.msg(message, { icon: 6 });
        },
        failMessage: function(message) {
            layer.msg(message, { icon: 2 });
        },
        errorMessage: function(message) {
            layer.msg(message, { icon: 5 });
        },
        successMessage: function(message) {
            layer.msg(message, { icon: 1 });
        },
        successAlert: function(msg) {
            layer.alert(msg, { icon: 1 });
        },
        failAlert: function(msg) {
            layer.alert(msg, { icon: 2 });
        }
    })

    // --------------------------------------
    // ajax
    // --------------------------------------
    $.extend({
        jsonResposneFunction: function(callback) {
            return function(response) {
                if (typeof response === 'string')
                    response = JSON.parse(response)
                var resStatus = response.status,
                    status = _RESPONSE_STATUS;

                if (status.NO_LOGIN === resStatus) {
                    $.errorMessage("请先登录")
                } else if (status.NO_PERMISSION === resStatus) {
                    $.errorMessage(response.message || "您没有权限访问该页面或执行该操作");
                } else if (status.ERROR === resStatus) {
                    $.errorMessage(response.message || "访问页面或执行操作错误");
                } else if (status.FAIL === resStatus) {
                    $.errorMessage(response.message || "操作失败");
                } else if (status.FAIL_VALID === resStatus) {
                    $.errorMessage(response.message || "验证不成功，操作失败");
                } else {
                    if (callback && typeof callback === 'function')
                        callback(response.result);
                }
            }
        },
        postJsonAjax: function(url, data, callback) {
            $.sendAjax({
                type: "POST",
                url: url,
                dataType: "json",
                data: JSON.stringify(data),
                contentType: "application/json",
                success: function(result) {
                    if (callback && typeof callback === 'function')
                        callback(result);
                }

            });
        },
        sendAjax: function(options) {
            var callback = options.success;
            options.success = $.jsonResposneFunction(callback);
            $.ajax(options);
        },
        getAjax: function(url, callback) {
            $.get(url, $.jsonResposneFunction(callback));
        },
        postAjax: function(url, data, callback) {

            if (typeof data === 'function') {
                callback = data;
                data = null;
            }
            $.post(url, data, $.jsonResposneFunction(callback));
        },
        locationPost: function(url, args) {
            var form = $("<form method='post' action='" + url + "'></form>");
            $.each(args, function(key, value) {
                var input = $("<input type='hidden'>");
                input.attr({ "name": key });
                input.val(value);
                form.append(input);
            });
            form.appendTo(document.body);
            form.submit();
            document.body.removeChild(form[0]);
        }
    });

    $.extend({
        addQueryCondition: function(param, el) {
            param = param || {};
            $(el).each(function(index) {
                var $this = $(this);
                var name = $this.attr("name");
                name = name || $this.attr("id");
                if (!name)
                    return;
                var value = $this.val();
                if (value === null)
                    return;

                var type = typeof(value);
                if (type === "string") {
                    value = value.trim();
                    if (value === "")
                        return;
                } else if (type === "undefined")
                    return;

                param[name] = value;

            });
        }

    });

    $.fn.toQueryCondition = function(param) {
        var $this = $(this);
        var name = $this.attr("name");
        name = name || $this.attr("id");
        if (!name)
            return;
        var value = $this.val();
        if (value === null)
            return;

        var type = typeof(value);
        if (type === "string") {
            value = value.trim();
            if (value === "")
                return;
        } else if (type === "undefined")
            return;

        param[name] = value;
    }

    // --------------------------------------
    // login
    // --------------------------------------

    // --------------------------------------
    // alert
    // --------------------------------------

    // --------------------------------------
    // 事件分发器
    // --------------------------------------

    $.namespace2win('tonto.event');

    var event_Dispatcher = function() {

    }

    event_Dispatcher.prototype.addEventListener = function(event, callback) {
        var map = this.listenerMap || (this.listenerMap = {});
        var listeners = map[event] || (map[event] = new Array());
        listeners.push(callback);
    }

    event_Dispatcher.prototype.distribute = function(event, data) {
        var map = this.listenerMap || (this.listenerMap = {});
        var listeners = map[event];
        if (listeners) {
            for (var i = 0; i < listeners.length; i += 1) {
                listeners[i].call(this, data);
            }
        }
    }

    tonto.event.Dispatcher = event_Dispatcher;

    // ------------------------------------------
    //
    //          常用业务控件和方法
    //
    // -----------------------------------------


    _initValidator();
    _initTable();
    _initEnumConstant();
    _initUnitComponment();
    _initAssessCycleComponment();
    _initForm();
    _initFromContent();

})(jQuery);


function _initValidator() {
    // --------------------------------------
    // Validate
    // --------------------------------------

    // this.optional(element) 指定了表单不为空才判断

    //自然数
    $.validator.addMethod("naturalNumber", function(value, element) { return this.optional(element) || (/^[1-9]\d{0,9}$/.test(value)); }, "请输入大于0小于9999999999的整数");
    //身份证
    $.validator.addMethod("identity", function(value, element) {
        var id = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;

        if (!id.test(value))
            return this.optional(element) || false;

        var y = value.substring(6, 10) * 1;
        var m = value.substring(10, 12) * 1 - 1;
        var d = value.substring(12, 14) * 1;

        var date = new Date(y, m, d);

        return this.optional(element) || (date.getFullYear() == y && date.getMonth() == m && date.getDate() == d);
    }, "身份证格式错误");
    //邮编
    $.validator.addMethod("zip", function(value, element) { return this.optional(element) || (/^[0-9]\d{5}$/.test(value)); }, "邮编格式错误");
    //账号
    $.validator.addMethod("account", function(value, element) { return this.optional(element) || (/^\w{5,30}$/.test(value)); }, "账号格式错误");
    //手机
    $.validator.addMethod("cellphone", function(value, element) { return this.optional(element) || (/^1[3|5|7|8|]\d{9}$/.test(value)); }, "手机号码格式错误");
    //电话（包括手机和座机）
    $.validator.addMethod("phone", function(value, element) { return this.optional(element) || (/((^\d{3,4}-?)?\d{7,8}$)|(^1[3|5|7|8|]\d{9}$)/.test(value)); }, "电话号码格式错误");
    $.validator.addMethod("englishName", function(value, element) { return this.optional(element) || (/^\w+$/.test(value)); }, "请输入英文名称");
    //日期
    $.validator.addMethod("date", function(value, element) {
        var r = value.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
        if (r == null)
            return this.optional(element);

        var d = new Date(r[1], r[3] - 1, r[4]);
        return this.optional(element) || (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4]);
    }, "日期格式不正确");
    //大于
    $.validator.addMethod("largeThan", function(value, element, $name) {
        if ($name) {
            var minVal = $($name).val();
            if (minVal) {
                if (("string" === typeof(minVal) && /\d+\.?\d*/.test(minVal)) || "number" === typeof(minVal))
                    return this.optional(element) || value >= minVal * 1;
            }
        }
        return true;
    }, "输入值不能小于最小值");
    $.validator.addMethod("maxEngLength", function(value, element, maxlength) { return this.optional(element) || (value.replace(/[^\x00-\xff]/g, 'xx').length <= maxlength); }, "输入长度不能超过{0}");
    $.validator.addMethod("minEngLength", function(value, element, minlength) { return this.optional(element) || (value.replace(/[^\x00-\xff]/g, 'xx').length >= minlength); }, "输入长度不能少于{0}");

    $.extend($.fn, {
        createValidate: function(config) {
            for (var i = 0; this.length > i; i++) {
                var target = $(this[i]);

                var name = target.attr("name");
                var rules = config && config.rules && config.rules[name] || {};
                var messages = config && config.messages && config.messages[name] || {};

                var title = $("label[for='" + name + "']").text();
                title = $.trim(title);
                if (title && title.endsWith(":")) {
                    title = title.substring(0, title.length - 1);
                }

                var type = target.attr("data-type");
                if (type) {
                    if (type.indexOf(" ") != -1) {
                        var ts = type.split(" ");
                        for (var j = 0; j < ts.length; j++) {
                            rules[ts[j]] = true;
                        }
                    } else {
                        rules[type] = true;
                    }
                }

                if (target.hasClass("required") || target.attr("required") == "required") {
                    rules.required = true;
                    target.addRequiredStyle();


                    if (!messages.required) {

                        var placeholder = target.attr("placeholder");
                        if (placeholder) {
                            messages.required = placeholder;
                        } else {
                            var domType = target[0].type;
                            if (domType == "text" || domType == "password") {
                                messages.required = "请输入" + title;
                            } else {
                                messages.required = "请选择" + title;
                            }
                        }

                    }
                }


                var area = target.attr("data-area");
                if (area) {
                    var numbers = area.split(",");
                    if (numbers.length > 0) {
                        if (numbers[0])
                            rules.min = numbers[0] * 1;
                        if (numbers.length > 1) {
                            if (numbers[1])
                                rules.max = numbers[1] * 1;
                        }
                    }
                }

                var length = target.attr("data-length");
                if (length) {
                    var lengths = length.split(",");
                    if (lengths) {
                        if (lengths.length > 0) {
                            if (lengths[0])
                                rules.minlength = lengths[0] * 1;
                            if (lengths.length > 1) {
                                if (lengths[1])
                                    rules.maxlength = lengths[1] * 1;
                            }
                        }
                    }
                }

                var length = target.attr("data-eng-length");
                if (length) {
                    var lengths = length.split(",");
                    if (lengths) {
                        if (lengths.length > 0) {
                            if (lengths[0])
                                rules.minEngLength = lengths[0] * 1;
                            if (lengths.length > 1) {
                                if (lengths[1])
                                    rules.maxEngLength = lengths[1] * 1;
                            }
                        }
                    }
                }

                var equalTo = target.attr("equalTo");
                if (equalTo) {
                    rules["equalTo"] = equalTo;
                }

                var largeThan = target.attr("large-than");
                if (largeThan) {
                    rules["largeThan"] = largeThan;
                    if (!messages.largeThan) {

                        var thanTarget = $(largeThan);
                        var thanName = thanTarget.attr("name");
                        var thanTitle = $("label[for='" + thanName + "']").text();
                        thanTitle = $.trim(thanTitle);
                        if (thanTitle && thanTitle.endsWith(":")) {
                            thanTitle = thanTitle.substring(0, thanTitle.length - 1);
                        }

                        if (thanTitle) {
                            messages.largeThan = "输入值必须大于" + thanTitle + "的值";
                        }

                    }
                }

                rules.messages = messages;
                target.rules("add", rules);
            }
        },
        createFormValidate: function(config) {
            var validater = this.validate(config);
            this.find("input[type='text']:enabled,input[type='password']:enabled,input[type='hidden']:enabled,select:enabled,textarea:enabled").createValidate(config);
            return validater;
        },
        validateElement: function(element) {
            var validater = $(this)[0].validater;
            return validater ? validater.element(element) : true;
        },
        addRequiredStyle: function() {
            var target = $(this);
            var inputGroupParent = target.parent(".input-group");
            if (inputGroupParent.length > 0) {
                inputGroupParent.children(":last-child").css("border-right", "2px solid red");
            } else {
                target.css("border-right", "2px solid red");
            }
        },
        removeRequiredStyle: function() {
            var target = $(this);
            var inputGroupParent = target.parent(".input-group");
            if (inputGroupParent.length > 0) {
                inputGroupParent.children(":last-child").css("border-right", "");
            } else {
                target.css("border-right", "");
            }
        }
    });
}


function _initTable() {
    /*
     * options 参数配置基于bootstrp table，参考
     * http://bootstrap-table.wenzhixin.net.cn/zh-cn/documentation/ 修改参数： url:
     * 可以为方法，返回具体url字符串
     * 
     * 其中对table tree做了处理，可以获取指定form中的request param
     */
    var _tonto_table = function(el, options) {

        var defaultOptions = $.fn.bootstrapTable.defaults;

        if (typeof options === 'string')
            options = {
                url: options
            };

        if (!options.ajax) {
            options.ajax = function(request) {
                if (typeof url === 'function')
                    request.url = request.url();
                $.sendAjax(request);
            }
        }

        if (options.columns) {
            options.columns.forEach(function(item) {

                if (!$.isArray(item)) {
                    item = [item];
                }
                item.forEach(function(col) {
                    if (col.formatter && typeof col.formatter === 'string') {
                        if (col.formatter == 'date') {
                            col.formatter = function(value, row, index) {
                                if (value) {
                                    if (!isNaN(value)) {
                                        return $.formatDate(new Date(value), "yyyy-MM-dd");
                                    }
                                    return value;
                                }
                                return "";
                            }
                        } else if (col.formatter == 'time') {
                            col.formatter = function(value, row, index) {
                                if (value) {
                                    if (isNaN(value)) {
                                        return $.formatDate(new Date(value), "yyyy-MM-dd hh:mm:ss");
                                    }
                                    return value;
                                }

                                return "";
                            }
                        }

                    }
                });

            });
        }

        var selfOptions = {
            sidePagination: 'server',
            dataField: 'data',
            totalField: 'total',
            treeParentField: 'parentId',
            pageList: [10, 20, 30],
            pageSize: 10
        }

        options = $.extend(selfOptions, options);

        if (options.searchbar) {
            var q = options.queryParams;
            options.queryParams = function(params) {
                if (q)
                    params = q(params);

                $(options.searchbar).find(
                        "input[type='text']:enabled," + "input[type='password']:enabled," +
                        "input[type='hidden']:enabled," + "input[type='checkbox']:enabled:checked," +
                        "input[type='radio']:enabled:checked," + "select:enabled," + "textarea:enabled")
                    .each(function() {
                        $(this).toQueryCondition(params);
                    });

                return params;
            };
        }

        if (options.treeView) {
            var rh = options.responseHandler;
            options.responseHandler = function(res) {
                // 因为treetable插件中写死了使用parentId，这里需要对返回结果处理下(可以改写插件)
                if (rh)
                    res = rh(res);
                res = res || [];

                var isArray = $.isArray(res);

                var dataField = options.dataField || defaultOptions.dataField;
                var totalField = options.totalField || defaultOptions.totalField;

                var data = isArray ? res : res[dataField];

                if ($.isArray(data)) {
                    var idArr = [];
                    data.forEach(function(item) {

                        item.parentId = item[options.treeParentField];

                        //如果支持搜索，则会有部分父节点没搜索出来（当然你可以只查询过滤叶节点），
                        //在这里会把没有父节点的节点parentId = null，因而造成数据可能会不完整，使用时候注意
                        if (options.treeParentFilter && item.parentId) {
                            var treeId = options.treeId || defaultOptions.treeId;
                            var b = false;
                            for (var i = 0; i < data.length; i++) {
                                var a = data[i];
                                if (a[treeId] == item.parentId) {
                                    b = true;
                                    break;
                                }
                            }

                            if (!b)
                                item.parentId = null;
                        }
                    });
                }

                if ($.isArray(res)) {
                    var x = {};
                    x[dataField] = res;
                    x[totalField] = res.length;
                    return x;
                } else {
                    return res;
                }
            };

            if (options.data) {
                options.responseHandler(options.data);
            }
        }

        if (!options.responseHandler) {
            // 用于判断对返回数据的简单处理，当结果是一个数组时对其封装为table能接收的格式
            options.responseHandler = function(res) {
                res = res || [];
                if ($.isArray(res)) {
                    var dataField = options.dataField || defaultOptions.dataField;
                    var totalField = options.totalField || defaultOptions.totalField;

                    var x = {};
                    x[dataField] = res;
                    x[totalField] = res.length;
                    return x;
                } else {
                    return res;
                }
            }
        }

        var $table = $(el);
        $table.bootstrapTable(options);
        return $table.data('bootstrap.table');
    };

    $.extend({
        /**
         * 创建boostrap table
         */
        createTable: function(el, options) {

            var tables = [];
            $(el).each(function(index) {
                tables[index] = new _tonto_table($(this), options);
            });

            if (tables.length == 1)
                return tables[0];
            return tables;
        },
        /**
         * 获取常量formatter方法，用于bootstrap table column
         */
        getEnumColumnFormatter: function(enumTypeMap, type) {

            if (enumTypeMap && type) {
                return function(value, row, index) {
                    var data = enumTypeMap[type];
                    if (data) {
                        for (var i = 0; i < data.length; i++)
                            if (data[i].key == value)
                                return data[i].value;
                    }
                    return "";
                };
            }
        },
        tableColumnFormatter: function(text, type, icon) {
            return function() { return '<a class="' + type + '" href="javascript:void(0);" ><i class="glyphicon glyphicon-' + (icon ? icon : 'cog') + '"></i>' + text + '</a>' };
        },
        addColumnFormatter: function(text) {
            return function() { return '<a class="add" href="javascript:void(0);" ><i class="glyphicon glyphicon-plus"></i>' + (text ? text : '新增') + '</a>' };
        },
        removeColumnFormatter: function(text) {
            return function() { return '<a class="remove" href="javascript:void(0);" ><i class="glyphicon glyphicon-remove"></i>' + (text ? text : '删除') + '</a>' };
        },
        editColumnFormatter: function(text) {
            return function() { return '<a class="edit" href="javascript:void(0);" ><i class="glyphicon glyphicon-edit"></i>' + (text ? text : '修改') + '</a>' };
        },
        viewColumnFormatter: function(text) {
            return function() { return '<a class="view" href="javascript:void(0);" ><i class="glyphicon glyphicon-search"></i>' + (text ? text : '查看') + '</a>' };
        },
        confirmColumnFormatter: function(text) {
            return function() { return '<a class="confirm" href="javascript:void(0);" ><i class="glyphicon glyphicon-edit"></i>' + (text ? text : '确认') + '</a>' };
        },
        eventColumnFormatter: function(text) {
            return function() { return '<a class="event" href="javascript:void(0);" ><i class="glyphicon glyphicon-wrench"></i>' + (text ? text : '事件维护') + '</a>' };
        },
        backwardColumnFormatter: function(text) {
            return function() { return '<a class="backward" href="javascript:void(0);" ><i class="glyphicon glyphicon-backward"></i>' + (text ? text : '退回') + '</a>' };
        }
    });

    $.fn.createTable = function() {

        var tables = [];

        this.each(function(index) {
            tables[index] = new _tonto_table($(this), options);
        });

        if (tables.length == 1)
            return tables[0];
        return tables;
    };
}


function _initFromContent(container) {

    var _isViewForm = false;
    var _isEditForm = false;
    var _isAddForm = false;

    /**
     * 自动根据FORM TYPE对表单做一些特定操作，这里对VIEW查看类型的FORM进行了readonly，disabled的操作 <class = tonto-form-type>
     */
    var formTypes = container ? $(container).find(".tonto-form-type") : $(".tonto-form-type");

    formTypes.each(function() {
        var type = $(this).val();
        if (type == "VIEW") {
            _isViewForm = true;

            var forms = container ? $(container).find("form") : $("form");
            forms.each(function() {
                $t = $(this);
                $t.find("textarea, input").attr({ readonly: true });
                $t.find(":submit").hide();
                $t.find("input[type='text']").attr('placeholder', null);
                $t.find("textarea").attr('placeholder', null);
                $t.find("button[type='button'],input[type='button'],select").attr('disabled', true);
                $t.find("a").hide();
            });
        } else if (type == "EDIT") {
            _isEditForm = true;
        } else if (type == "ADD") {
            _isAddForm = true;
        }
    });


    // ----------------------------------------------------------
    //
    //  以下为查看，修改，添加表单时候各自的一些自动加载
    //
    // ----------------------------------------------------------

    /**
     * 加载时间控件 <class = tonto-datepicker-date>
     */

    if (!_isViewForm) {
        var dates = container ? $(container).find(".tonto-datepicker-date") : $(".tonto-datepicker-date");
        dates.each(function() {
            var $t = $(this);
            $t.datepicker({
                language: "zh-CN",
                autoclose: true, //选中之后自动隐藏日期选择框
                //clearBtn: true, //清除按钮
                //todayBtn: true, //今日按钮
                format: "yyyy-mm-dd" //日期格式，详见 http://bootstrap-datepicker.readthedocs.org/en/release/options.html#format
            });

            $t.attr("readonly", true);
            $t.css("background", "#fff");
        })
    }

    if (_isEditForm) {
        var editUnables = container ? $(container).find(".tonto-edit-unable") : $(".tonto-edit-unable");
        editUnables.each(function() {
            var $t = $(this);
            $t.attr('placeholder', null);
            $t.attr('disabled', true);
        });
    }

    if (_isViewForm) {
        var a = $(".tonto-select-assess-cycle,.tonto-select-assess-cycle-self,.tonto-select-assess-cycle-user,.tonto-select-assess-cycle-unit,.tonto-select-department,.tonto-select-agency,.tonto-select-unit");
        a.css("background", "#eee");
        a.prop("onclick", null).off("click");
    }
}

var _constant_cache = {};
/**
 * 自动加载常量下拉框 <class = tonto-select-constant>
 */
function _initEnumConstant(container) {

    $.extend({
        // 获取常量
        getConstantEnum: function(enumcode, callback) {

            var getTargetCallback = function(param) {
                var that = $(param.target);
                var codeValue = that.attr("enum-code-value");
                var code = param.code;
                var type = param.type || (that[0].type.startsWith("select") ? "select" : "input");
                return function(map) {
                    var enumvalues = map[code];
                    if (enumvalues) {
                        if (type == 'select') {
                            if (enumvalues) {
                                enumvalues.forEach(function(a) {
                                    that.append("<option value='" + a.key + "'>" + a.value + "</option>");
                                });
                            }
                            if (codeValue) {
                                that.val(codeValue);
                            }
                        } else if (type == 'input') {
                            if (codeValue) {
                                enumvalues.forEach(function(a) {
                                    if (a.key == codeValue) {
                                        that.val(a.value);
                                        return false;
                                    }
                                });
                            }
                        } else if (type == 'p') {
                            if (codeValue) {
                                enumvalues.forEach(function(a) {
                                    if (a.key == codeValue) {
                                        that.html(a.value);
                                        return false;
                                    }
                                });
                            }
                        }
                    }
                }
            }

            // 先从缓存中获取，没有再去后台取
            window._constant_cache = _constant_cache || {};

            var targetCallbacks = [];

            var url = "/system/constants/enum";
            if (!$.isArray(enumcode)) {
                var code;
                if (typeof enumcode == 'string') {
                    code = enumcode;
                } else {
                    code = enumcode.code;
                    targetCallbacks.push(getTargetCallback(enumcode));
                }

                if (_constant_cache[code]) {
                    if (callback) {
                        callback(_constant_cache);
                    }

                    targetCallbacks.forEach(function(f) {
                        f(_constant_cache);
                    });

                    return;
                }

                url += "?code=" + code;

            } else {
                url += "?";
                var i = 0;

                enumcode.forEach(function(item) {
                    var code;
                    if (typeof item == 'string') {
                        code = item;
                    } else {
                        code = item.code;
                        targetCallbacks.push(getTargetCallback(item));
                    }

                    if (!_constant_cache[code]) {
                        url += "code=" + code + "&";
                        i++;
                    }
                });

                if (i == 0) {
                    callback(_constant_cache);
                    targetCallbacks.forEach(function(f) {
                        f(_constant_cache);
                    });
                    return;
                }
            }

            $.getAjax(url, function(data) {
                $.extend(_constant_cache, data);
                targetCallbacks.forEach(function(f) {
                    f(_constant_cache);
                });

                if (typeof callback === 'function') {
                    callback(_constant_cache);
                }
            });
        },
        getConstantEnumItem: function(enumcode, key) {
            var items = window._constant_cache[enumcode]
            for (var i = 0; i < items.length; i++) {
                if (items[i].key == key) {
                    return items[i];
                }
            }

            return null;
        }
    });

    var constants = container ? container.find(".tonto-select-constant") : $(".tonto-select-constant");
    var _enumKeys = [];

    constants.each(function() {
        var enumcode = $(this).attr("enumcode");
        if (enumcode) {
            _enumKeys.push(enumcode);
        }
    });

    if (_enumKeys.length > 0) {
        $.getConstantEnum(_enumKeys, function(data) {
            constants.each(function() {
                var $s = $(this);
                var enumcode = $s.attr("enumcode");
                if (enumcode) {
                    var enumvalues = data[enumcode];
                    if (enumvalues) {
                        enumvalues.forEach(function(a) {
                            $s.append("<option value='" + a.key + "'>" + a.value + "</option>");
                        });
                    }
                    var selectedvalue = $s.attr("selectedvalue");
                    if (selectedvalue) {
                        $s.val(selectedvalue);
                    }
                }
            });
        });
    }
}


/**
 * 创建UNIT 控件
 */
function _createUnitComponment(input, type, callback) {
    var $input = $(input);
    var $wrap = $('<div class="input-group"/>');
    var name = $input.attr("name") || $input.attr("id");
    $input.attr("name", "_" + name);
    var $hideinput = $('<input type="text" style="display:none" name="' + name + '" id="' + name + '"  />');
    var $removeBtn = $('<span class="input-group-addon" style="cursor:pointer"><i class="glyphicon glyphicon-remove"> </i></span>');
    var defaultValue = $input.attr("unitid");

    if (!callback || !(typeof callback == 'function')) {
        callback = $input.attr("unit-callback");
        callback = callback ? $.getWindowFunction(callback) : null;
    }

    $input.attr("readonly", true);
    $input.css("background", "#fff");

    $input.wrap($wrap);
    $input.after($removeBtn);
    $input.after($hideinput);

    var com = {
        input: $input,
        defaultValue: defaultValue,
        callback: callback,
        removeBtn: $removeBtn,
        name: name,
        valueInput: $hideinput,
        current: null,
        type: type,
        treedata: null,
        setCurrent: function(val) {
            var that = this;
            that.current = val;
            that.input.val(val ? val.name : "");
            that.valueInput.val(val ? val.id : "");

            if (that.callback)
                that.callback(val);
        },
        setEnabled: function(enabled) {
            if (enabled) {
                this.input.attr('disabled', false);
                this.valueInput.attr('disabled', false);
                this.input.css("background", "#fff");
            } else {
                this.input.attr('disabled', true);
                this.valueInput.attr('disabled', true);
                this.input.css("background", "#eee");
            }
        },
        setData: function(data) {
            var that = this;
            var treedata;
            var defaultValue = that.defaultValue;
            var type = that.type;
            var callback = that.callback;
            var current;
            var ownunit;
            var isAgency;

            var required = ((that.input.hasClass("required") || that.input.attr("required") == "required"));

            if (type == 'department' || type == "unit") {

                ownunit = data.ownIds || [];
                var uniqueUnit;
                var isUnique = false;

                var g = function(units, isall) {
                    var nodes = [];
                    units.forEach(function(unit) {
                        var node = {
                            text: unit.name,
                            data: unit
                        };

                        if (defaultValue && defaultValue == unit.id) {
                            current = unit;
                        }

                        if (ownunit.length == 1 && ownunit[0] == unit.id) {
                            uniqueUnit = unit;
                        }

                        if (unit.children) {
                            node.nodes = g(unit.children,isall);
                            if (node.nodes.length == 0) {
                                node.nodes = null;
                            }
                        }
                        nodes.push(node);
                    });

                    if (!isall) {
                        nodes = $.grep(nodes, function(n, i) {
                            if (!n.nodes || n.nodes.length == 0) {
                                return $.inArray(n.data.id, ownunit) != -1;
                            }
                            return true;
                        });
                    }

                    return nodes;
                }

                if (data.agency) {
                    treedata = g(data.agency, true);
                    isAgency = true;
                } else if (data.departments) {
                    treedata = g(data.departments, false);

                    if (!current && required && ownunit.length == 1) {
                        // 如果必填并且只有一个值，默认填入
                        current = uniqueUnit;
                        isUnique = true;
                    }
                }
            } else if (type == "agency") {
                treedata = [];

                data.forEach(function(a) {

                    treedata.push({
                        text: a.name,
                        data: a.id
                    });

                    if (defaultValue && defaultValue == a.id) {
                        current = a;
                    }
                });

                if (!current && required && data.length == 1) {
                    // 如果必填并且只有一个值，默认填入
                    current = data[0];
                    isUnique = true;
                }
            }

            that.setCurrent(current);

            that.removeBtn.on("click", function() {
                that.setCurrent(null);
            });

            that.input.click(function() {

                layer.open({
                    type: 1,
                    title: "单位",
                    content: "<div class='tonto-unit-check-div'></div>",
                    area: ['350px', '460px'],
                    success: function(layero, index) {
                        $tree = $(layero).find('.tonto-unit-check-div');

                        $tree.treeview({
                            data: treedata,
                            levels: 2
                        });

                        $tree.on('nodeSelected', function(event, data) {

                            var item = data.data;

                            if (type == "department") {
                                if (!item.parentId) {
                                    $.infoMessage("请选择一个科室");
                                    return;
                                } else {
                                    if (!isAgency && ($.inArray(item.id, ownunit) == -1)) {
                                        $.infoMessage("您不能选择该科室");
                                        return;
                                    }
                                }
                            } else if (type == "unit") {
                                if (!isAgency && ($.inArray(item.id, ownunit) == -1)) {
                                    $.infoMessage("您不能选择该科室");
                                    return;
                                }
                            } else if (type == "agency") {
                                item = {
                                    id: item,
                                    name: data.text
                                }
                            }

                            that.setCurrent(item);
                            layer.close(index);
                        });
                    }
                });
            });
        },
        loadData: function(url) {
            var that = this;
            if (!url) {
                url = (that.type == 'agency') ? "/org/unit/own/agency" : "/org/unit/own/department";
            }
            $.postAjax(url, function(data) {
                that.setData(data);
            });
        }
    }


    $input[0].unitComponment = com;

    return com;
}


/**
 * 自动加载初始化单位选择表单控件 <class = tonto-select-unit>
 */
function _initUnitComponment(container) {

    /**
     * 获取UNIT控件
     */
    $.extend($.fn, {
        getUnitComponment: function() {
            return $(this)[0].unitComponment;
        }
    });

    var _tonto_depart = container ? $(container).find(".tonto-select-department") : $(".tonto-select-department");
    var _tonto_agencys = container ? $(container).find(".tonto-select-agency") : $(".tonto-select-agency");
    var _tonto_units = container ? $(container).find(".tonto-select-unit") : $(".tonto-select-unit");

    if (_tonto_units.length > 0) {
        var coms1 = [];
        for (var i = 0; i < _tonto_units.length; i++) {
            coms1.push(_createUnitComponment(_tonto_units[i], "unit"));
        }
        $.postAjax("/org/unit/own/department", function(data) {
            for (i = 0; i < coms1.length; i++) {
                coms1[i].setData(data);
            }
        });
    }

    if (_tonto_depart.length > 0) {
        var coms2 = [];
        for (var i = 0; i < _tonto_depart.length; i++) {
            coms2.push(_createUnitComponment(_tonto_depart[i], "department"));
        }
        $.postAjax("/org/unit/own/department", function(data) {
            for (i = 0; i < coms2.length; i++) {
                coms2[i].setData(data);
            }
        });
    }

    if (_tonto_agencys.length > 0) {
        var coms3 = [];
        for (var i = 0; i < _tonto_agencys.length; i++) {
            coms3.push(_createUnitComponment(_tonto_agencys[i], "agency"));
        }
        $.postAjax("/org/unit/own/agency", function(data) {
            for (i = 0; i < coms3.length; i++) {
                coms3[i].setData(data);
            }
        });
    }

}


/**
 * 创建考评周期选择控件
 */
function _createAssessCycleComponment(input, _options, callback) {

    var options = {
        type: 1
    }

    if (typeof _options == 'function') {
        callback = _options;
    } else if (typeof _options == 'object') {
        $.extend(options, _options);
    } else {
        options.type = _options * 1;
    }

    var $input = $(input);
    var required = ($input.hasClass("required") || $input.attr("required") == "required");
    var $wrap = $('<div class="input-group"/>');
    var name = $input.attr("name") || $input.attr("id");
    $input.attr("name", "_" + name);
    var $hideinput = $('<input type="text" style="display:none" name="' + name + '"  />');
    var $removeBtn = required ? $('<span class="input-group-addon" style="cursor:pointer"><i class="glyphicon glyphicon-chevron-down"> </i></span> ') :
        $('<span class="input-group-addon" style="cursor:pointer"><i class="glyphicon glyphicon-remove"> </i></span> ');
    var defaultValue = $input.attr("cycleid");
    var defaultName = $input.attr("cyclename");
    var userId = options.userId || $input.attr("userId");
    var unitId = options.unitId || $input.attr("unitId");
    var type = options.type;


    if (!callback && !(typeof callback == 'function')) {
        callback = $input.attr("assess-cycle-callback");
        callback = callback ? $.getWindowFunction(callback) : null;
    }

    $input.attr("readonly", true);
    $input.css("background", "#fff");

    $input.wrap($wrap);
    $input.after($removeBtn);
    $input.after($hideinput);

    var content = '<section class="tonto-layer-div content table-content">' +
        '    <table id="_tontoCycleTable" class="table table-hover"></table>' +
        '    <div style="width:200px" id="_tontoCycleTableTooler">' +
        //(type == 4 ? '        <input type="text" class="form-control tonto-select-agency" required="required" name="unitId" placeholder="请选择机构"></input>':'') +
        '    </div>' +
        '</section>';

    var com = {
        input: $input,
        callback: callback,
        removeBtn: $removeBtn,
        required: required,
        content: content,
        name: name,
        userId: userId,
        unitId: unitId,
        type: options.type,
        valueInput: $hideinput,
        setCurrent: function(row) {
            var that = this;
            if (row) {
                that.input.val(row.cycleName);
                that.valueInput.val(row.id);
            } else {
                that.input.val("");
                that.valueInput.val("");
            }

            if (that.callback)
                that.callback(row);
        },
        open: function() {

            var that = this;
            that.first = true;
            var type = that.type;

            layer.open({
                type: 1,
                title: ' ',
                maxmin: true, //开启最大化最小化按钮
                content: that.content,
                area: ['800px', '600px'],
                success: function(layero, index) {

                    var url = '/console/asscyc/select/' + (type == 1 ? 'self' : (type == 2 ? 'user' : (type == 3 ? 'unit' : (type == 4 ? 'assess' : 'selfenabled'))));

                    /*
                     * 如果是个人的 self=true,则应该查找自己所在机构的数据
                     */

                    that.table = $.createTable("#_tontoCycleTable", {
                        idField: "id",
                        columns: [
                            [
                                { title: "单位名称", field: "unitName" },
                                { title: "周期名称", field: "cycleName" },
                                { title: "考评类型", field: "assessType", formatter: function(value, row) { return value == "1" ? "年中测评" : "年终考评" } },
                                { title: "周期状态", field: "cycleState", formatter: function(value, row) { return value == "1" ? "启用" :(value == "2" ? "暂存" : ( value == "4" ? "停用" : "归档"))}},
                                { title: "周期开始时间", field: "cycleStartTime", formatter: "date" },
                                { title: "周期截止时间", field: "cycleEndTime", formatter: "date" }
                            ]
                        ],
                        url: url,
                        searchbar: type == 4 ? '#_tontoCycleTableTooler' : null,
                        sortName: 'createTime',
                        sortOrder: 'desc',
                        pagination: true,
                        toolbar: "#_tontoCycleTableTooler",
                        showRefresh: true,
                        onClickRow: function(row, element) {
                            com.setCurrent(row);
                            layer.close(index);
                        }
                    });

                    var unitInput = $(layero).find('[name="unitId"]');
                    // if (type == 4) {
                    //     // $.getAjax("/org/unit/self/agency", function(data) {
                    //     //     unitInput.attr("readonly", true);
                    //     //     unitInput.val(data.name);
                    //     // })

                    //     _createUnitComponment(unitInput, "agency", function() {
                    //         if (that.first) {
                    //             that.first = false;
                    //         } else {
                    //             that.table.refresh();
                    //         }
                    //     }).loadData();
                    // }
                }
            });
        }
    }

    $input.click(function() {
        com.open();
    });

    $removeBtn.on("click", function() {
        if (required) {
            com.open();
        } else {
            com.setCurrent(null);
        }

    });

    if (defaultValue) {
        if (defaultName) {
            com.setCurrent({
                cycleName: defaultName,
                id: defaultValue
            });
        } else {
            $.getAjax("/console/asscyc/get?id=" + defaultValue, function(data) {
                com.setCurrent(data);
                com.defaultValue = data;
            });
        }
    }



    $input[0].cycleComponment = com;

    return com;
}

/**
 * 自动加载初始化考评周期选择表单控件 <class = tonto-select-assess-cycle>
 */
function _initAssessCycleComponment() {
    $(".tonto-select-assess-cycle").each(function() {
        _createAssessCycleComponment($(this), 4);
    });

    $(".tonto-select-assess-cycle-self").each(function() {
        _createAssessCycleComponment($(this), 1);
    });

    $(".tonto-select-assess-cycle-user").each(function() {
        _createAssessCycleComponment($(this), 2);
    });

    $(".tonto-select-assess-cycle-unit").each(function() {
        _createAssessCycleComponment($(this), 3);
    });
    
    $(".tonto-select-assess-cycle-self-enabled").each(function() {
        _createAssessCycleComponment($(this), 5);
    });
}


/**
 * 加载form表单验证 <class = tonto-form-validate>
 */
function _initForm(container) {

    /**
     * 与ajax-form-submit结合处理子窗口提交form后回调（例如关闭子窗口并刷新父窗口表格）
     */
    $.extend({
        setLayerSubmitHandler: function(layero, index, submitSuccess, msg) {
            var forms = layer.getChildFrame('form', index);
            if (forms && forms.length > 0) {
                forms[0].layerSubmitHandler = function(data) {
                    if (submitSuccess && typeof submitSuccess === 'function') {
                        if (msg) {
                            layer.msg(msg, { icon: 1 });
                            submitSuccess(data);
                        } else submitSuccess(data);
                    }

                };
            }
        }
    });

    $.fn.setFormSubmitHandler = function(submitSuccess, msg) {
        var form = $(this);

        if (typeof submitSuccess == 'string') {
            form[0].submitHandler = function() {
                $.infoMessage(submitSuccess);
            }
        }

        if (msg) {
            form[0].submitHandler = function(data) {
                $.infoMessage(msg);
                submitSuccess(data);
            };
        } else {
            form[0].submitHandler = submitSuccess;
        }


    };

    var forms = container ? $(container).find(".tonto-form-validate") : $(".tonto-form-validate");
    forms.each(function() {
        var $submitForm = $(this);

        var $submitBtn = $submitForm.find('button[type="submit"],input[type="submit"]')

        $submitBtn.each(function() {
            var that = $(this);
            that.on('click', function(e) {
                if (that.data("loading")) {
                    return;
                }
                // ie处理placeholder提交问题
                if ($.browser && $.browser.msie) {
                    $submitForm.find('[placeholder]').each(function() {
                        var $input = $(this);
                        if ($input.val() == $input.attr('placeholder')) {
                            $input.val('');
                        }
                    });
                }
                return true;
            });
        });


        var config = {
            debug: true,
            // 不要设置true，只有不想启用时候去设置false
            // 是否在获取焦点时验证
            // onfocusout: false,
            // 在keyup时验证.
            onkeyup: false,
            // 当鼠标掉级时验证
            onclick: false,
            // 给未通过验证的元素加效果,闪烁等
            // highlight : false,
            showErrors: function(errorMap, errorList) {
                var msg = "";
                $.each(errorList, function(i, v) {
                    //msg += (v.message + "\r\n");
                    //在此处用了layer的方法,显示效果更美观
                    layer.tips(v.message, v.element, { time: 2000, tips: [3, 'red'] });
                    return false;
                });
            },
            submitHandler: function(form) {
                var $form = $(form);
                $form.ajaxSubmit({
                    url: $submitBtn.data('action') ? $submitBtn.data('action') : $form.attr('action'),
                    dataType: 'json',
                    beforeSubmit: function(arr, $form, options) {
                        $submitBtn.each(function() {
                            var that = $(this);
                            that.data("loading", true);
                            var text = that.text();
                            that.text(text + '中...').prop('disabled', true).addClass('disabled');
                        });
                    },
                    success: function(data) {

                        $submitBtn.each(function() {
                            var that = $(this);
                            var text = that.text();
                            that.removeClass('disabled').prop('disabled', false).text(text.replace('中...', '')).parent().find('span').remove();
                        });

                        var resStatus = data.status,
                            status = _RESPONSE_STATUS;

                        if (status.NO_LOGIN === resStatus) {
                            $.errorMessage("请先登录");
                        } else if (status.NO_PERMISSION === resStatus) {
                            $.errorMessage(data.message || "您没有权限访问该页面或执行该操作");
                        } else if (status.ERROR === resStatus) {
                            $.errorMessage(data.message || "访问页面或执行操作错误");
                        } else if (status.FAIL === resStatus) {
                            $.errorMessage(data.message || "操作失败");
                        } else if (status.FAIL_VALID === resStatus) {
                            error = data.result;
                            if ($.isArray(error)) {
                                error.forEach(function(item) {
                                    var el = item[1];
                                    var errorMsg = item[2];
                                    $form.find("#" + el + ",[name='" + el + "']").each(function() {
                                        layer.tips(errorMsg, $(this), { time: 2000, tips: [3, 'red'] });
                                    });
                                });
                            }
                        } else if (status.SUCCESS === resStatus) {
                            if ($form[0].layerSubmitHandler) {
                                $form[0].layerSubmitHandler(data.result ? data.result : data);
                                return;
                            }
                            if ($form[0].submitHandler) {
                                $form[0].submitHandler(data.result ? data.result : data);
                                return;
                            }
                        }
                    },
                    error: function(xhr, e, statusText) {
                        $.errorMessage("系统异常");
                    },
                    complete: function() {
                        $submitBtn.data("loading", false);
                    }
                });
            }
        };

        if (window.validateFormConfig)
            config = $.extend(config, window.validateFormConfig);

        var backurl = $submitForm.attr("callback-url");
        if (backurl && !$submitForm[0].submitHandler) {
            $submitForm.setFormSubmitHandler(function() {
                layer.alert("操作成功", function(index) {
                    layer.close(index);
                    window.location = backurl;
                })
            });
        }

        var validater = $submitForm.createFormValidate(config);
        $submitForm[0].validater = validater;
    });
}
