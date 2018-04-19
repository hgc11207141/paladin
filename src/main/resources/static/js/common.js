(function($) {

    // --------------------------------------
    // common
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
        formatDate: function(date, format) {

            var o = {
                "M+": date.getMonth() + 1, // month
                "d+": date.getDate(), // day
                "h+": date.getHours(), // hour
                "m+": date.getMinutes(), // minute
                "s+": date.getSeconds(), // second
                "q+": Math.floor((date.getMonth() + 3) / 3), // quarter
                "S": date.getMilliseconds() // millisecond
            }
            if (/(y+)/.test(format)) format = format.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (var k in o)
                if (new RegExp("(" + k + ")").test(format))
                    format = format.replace(RegExp.$1,
                        RegExp.$1.length == 1 ? o[k] :
                        ("00" + o[k]).substr(("" + o[k]).length));
            return format;
        },
        getUrlVariable: function(variable) {
            var query = window.location.search.substring(1);
            var vars = query.split("&");
            for (var i = 0; i < vars.length; i++) {
                var pair = vars[i].split("=");
                if (pair[0] == variable) { return pair[1]; }
            }
            return false;
        }
    });

    $.fn.getFormParams = function(param) {

        $(this).each(function() {
            $(this).find(
                    "input[type='text']:enabled," + "input[type='password']:enabled," +
                    "input[type='hidden']:enabled," + "input[type='checkbox']:enabled:checked," +
                    "input[type='radio']:enabled:checked," + "select:enabled," + "textarea:enabled")
                .each(function() {
                    var that = $(this);
                    var name = that.attr("name");
                    name = name || that.attr("id");
                    if (!name)
                        return;
                    var value = that.val();
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
        });
    };





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
     * 基于layer组件提供弹框消息，参考 http://www.layui.com/doc/modules/layer.html
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
        successAlert: function(msg, fun) {
            layer.alert(msg, { icon: 1 }, fun);
        },
        failAlert: function(msg, fun) {
            layer.alert(msg, { icon: 2 }, fun);
        },
        errorAlert: function(msg, fun) {
            layer.alert(msg, { icon: 5 }, fun);
        },
        infoAlert: function(msg, fun) {
            layer.alert(msg, { icon: 6 }, fun);
        }
    })


    // --------------------------------------
    // ajax
    // --------------------------------------

    $.extend({
        ajaxUnLoginHandler: function(callback) {
            // ajax请求返回未登录状态处理
            // 暂时跳转主页面到登录页面，有时间可以做弹出登录窗口登录，成功后继续执行ajax请求处理

            $.failAlert("请先登录", function() {
                top.location.href = "/health/login";
            })

        },
        wrapAjaxSuccessCallback: function(callback, submitBtn) {

            if (callback && typeof callback != 'function' && !submitBtn) {
                submitBtn = callback;
            }

            if (submitBtn) {
                $(submitBtn).each(function() {
                    var that = $(this);
                    that.data("loading", true);
                    var text = that.text();
                    that.data("orginText", text);
                    that.text(text + '中...').prop('disabled', true).addClass('disabled');
                });
            }

            // 包装Ajax成功回调方法，过滤返回内容
            return function(response) {
                if (submitBtn) {
                    $(submitBtn).each(function() {
                        var that = $(this);
                        var text = that.text();
                        that.removeClass('disabled').prop('disabled', false).text(that.data("orginText"));
                    });
                }

                if (typeof response === 'string') {
                    response = JSON.parse(response)
                }
                var resStatus = response.status,
                    status = _RESPONSE_STATUS;

                if (status.NO_LOGIN === resStatus) {
                    $.ajaxUnLoginHandler(callback);
                } else if (status.NO_PERMISSION === resStatus) {
                    $.errorMessage(response.message || "您没有权限访问该页面或执行该操作");
                } else if (status.ERROR === resStatus) {
                    $.errorMessage(response.message || "访问页面或执行操作错误");
                } else if (status.FAIL === resStatus) {
                    $.errorMessage(response.message || "操作失败");
                } else if (status.FAIL_VALID === resStatus) {
                    $.errorMessage(response.message || "验证不成功，操作失败");
                } else {
                    if (callback && typeof callback === 'function') {
                        callback(response.result);
                    }
                }
            }
        },
        postJsonAjax: function(url, data, callback, submitBtn) {

            if (callback && typeof callback != 'function' && !submitBtn) {
                submitBtn = callback;
            }

            // 发送json格式ajax请求
            $.sendAjax({
                type: "POST",
                url: url,
                dataType: "json",
                data: JSON.stringify(data),
                contentType: "application/json",
                success: function(result) {
                    if (callback && typeof callback === 'function')
                        callback(result);
                },
                submitBtn: submitBtn
            });
        },
        sendAjax: function(options) {
            // 发送ajax请求 对应$.ajax()
            var callback = options.success;
            options.success = $.wrapAjaxSuccessCallback(callback, options.submitBtn);
            $.ajax(options);
        },
        getAjax: function(url, callback, submitBtn) {
            // 对应$.get()
            $.get(url, $.wrapAjaxSuccessCallback(callback, submitBtn));
        },
        postAjax: function(url, data, callback, submitBtn) {
            // 对应$.post()
            if (typeof data === 'function') {
                callback = data;
                data = null;
            }
            $.post(url, data, $.wrapAjaxSuccessCallback(callback, submitBtn));
        },
        postFormAjax: function(url, args) {
            // 提交表单形式ajax
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
    // 常用业务控件和方法
    //
    // -----------------------------------------


    _initValidator();
    _initTable();
    _initEnumConstant();
    _initForm();


    // ------------------------------------------
    //
    // 页面处理
    //
    // -----------------------------------------

    $.fn.showInfoDescription = function(items, isHorizontal) {
        _showInfoDescription(items, $(this), isHorizontal);
    }


    // -----------------------------------------
    //
    // cache
    //
    // -----------------------------------------

    window.mycache = {};

    $.extend({
        putCache: function(key, value) {
            window.mycache[key] = value;
        },
        getCache: function(key) {
            return window.mycache[key];
        }
    })


})(jQuery);


function _initValidator() {
    // --------------------------------------
    // Validate
    // --------------------------------------

    // this.optional(element) 指定了表单不为空才判断

    // 自然数
    $.validator.addMethod("naturalNumber", function(value, element) { return this.optional(element) || (/^[1-9]\d{0,9}$/.test(value)); }, "请输入大于0小于9999999999的整数");
    // 身份证
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
    // 邮编
    $.validator.addMethod("zip", function(value, element) { return this.optional(element) || (/^[0-9]\d{5}$/.test(value)); }, "邮编格式错误");
    // 账号
    $.validator.addMethod("account", function(value, element) { return this.optional(element) || (/^\w{5,30}$/.test(value)); }, "账号格式错误");
    // 手机
    $.validator.addMethod("cellphone", function(value, element) { return this.optional(element) || (/^1[3|5|7|8|]\d{9}$/.test(value)); }, "手机号码格式错误");
    // 电话（包括手机和座机）
    $.validator.addMethod("phone", function(value, element) { return this.optional(element) || (/((^\d{3,4}-?)?\d{7,8}$)|(^1[3|5|7|8|]\d{9}$)/.test(value)); }, "电话号码格式错误");

    // 日期
    $.validator.addMethod("date", function(value, element) {
        var r = value.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
        if (r == null)
            return this.optional(element);

        var d = new Date(r[1], r[3] - 1, r[4]);
        return this.optional(element) || (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4]);
    }, "日期格式不正确");

    // 大于
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
        createElementValidater: function(config, requiredStyle) {
            // 创建元素验证器
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

                    if (requiredStyle) {
                        target.addRequiredStyle();
                    }

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
        createFormValidater: function(config) {
            // 创建表单验证器
            var validater = this.validate(config);
            this.find("input[type='text']:enabled,input[type='password']:enabled,input[type='hidden']:enabled,select:enabled,textarea:enabled").createElementValidater(config);
            this.data("validater", validater);
            return validater;
        },
        validateElement: function(element) {
            // 验证某个元素
            var validater = $(this).data("validater");
            return validater ? validater.element(element) : true;
        },
        addRequiredStyle: function() {
            // 添加必填样式
            var target = $(this);
            var inputGroupParent = target.parent(".input-group");
            if (inputGroupParent.length > 0) {
                inputGroupParent.children(":last-child").css("border-right", "2px solid red");
            } else {
                target.css("border-right", "2px solid red");
            }
        },
        removeRequiredStyle: function() {
            // 移除必填样式
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

                // 对列统一处理

                if (!$.isArray(item)) {
                    item = [item];
                }

                item.forEach(function(col) {
                    // formatter 定义数据类型转换，例如time，date等，在这里定义
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
                                    var time;
                                    if (isNaN(value)) {
                                        time = new Date(value);
                                    } else {
                                        time = new Date();
                                        time.setTime(value);
                                    }

                                    return $.formatDate(time, "yyyy-MM-dd hh:mm:ss");
                                }

                                return "";
                            }
                        } else if (col.formatter == 'boolean') {
                            col.formatter = function(value, row, index) {
                                return (value === true || value === "true") ? "是" : "否";
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
                if (q) {
                    params = q(params);
                }

                $(options.searchbar).getFormParams(params);

                return params;
            };
        }

        if (options.treeView) {

            // 对树状table支持，需要treetable.js

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
                        // 如果支持搜索，则会有部分父节点没搜索出来（当然你可以只查询过滤叶节点），
                        // 在这里会把没有父节点的节点parentId = null，因而造成数据可能会不完整，使用时候注意
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

                            if (!b) {
                                item.parentId = null;
                            }
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
                tables.push(new _tonto_table($(this), options));
            });

            return tables.length == 1 ? tables[0] : tables;
        },
        /**
         * 获取常量formatter方法，用于bootstrap table column *
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
        }
    });

    $.fn.createTable = function() {
        var tables = [];
        this.each(function() {
            tables.push(new _tonto_table($(this), options));
        });
        return tables.length == 1 ? tables[0] : tables;
    };

    // 页码本地化

    $.fn.bootstrapTable.locales['zh-CN'] = {
        formatLoadingMessage: function() {
            return '正在努力地加载数据中，请稍候……';
        },
        formatRecordsPerPage: function(pageNumber) {
            return pageNumber;
        },
        formatShowingRows: function(pageFrom, pageTo, totalRows) {
            return '显示' + pageFrom + ' - ' + pageTo + '条 ，共 ' + totalRows + ' 条';
        },
        formatSearch: function() {
            return '搜索';
        },
        formatNoMatches: function() {
            return '没有找到匹配的记录';
        },
        formatPaginationSwitch: function() {
            return '隐藏/显示分页';
        },
        formatRefresh: function() {
            return '刷新';
        },
        formatToggle: function() {
            return '切换';
        },
        formatColumns: function() {
            return '列';
        },
        formatExport: function() {
            return '导出数据';
        },
        formatClearFilters: function() {
            return '清空过滤';
        }
    };

    $.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales['zh-CN']);

    // 修改排序部分代码，使sortName起效
    if ($.fn.bootstrapTable) {
        var BootstrapTable = $.fn.bootstrapTable.Constructor;
        BootstrapTable.prototype.onSort = function(event) {
            var $this = event.type === "keypress" ? $(event.currentTarget) : $(event.currentTarget).parent(),
                $this_ = this.$header.find('th').eq($this.index()),
                sortName = this.header.sortNames[$this.index()];

            this.$header.add(this.$header_).find('span.order').remove();

            if (this.options.sortName === $this.data('field')) {
                this.options.sortOrder = this.options.sortOrder === 'asc' ? 'desc' : 'asc';
            } else {
                this.options.sortName = sortName || $this.data('field');
                this.options.sortOrder = $this.data('order') === 'asc' ? 'desc' : 'asc';
            }
            this.trigger('sort', this.options.sortName, this.options.sortOrder);

            $this.add($this_).data('order', this.options.sortOrder);

            // Assign the correct sortable arrow
            this.getCaret();

            if (this.options.sidePagination === 'server') {
                this.initServer(this.options.silentSort);
                return;
            }

            this.initSort();
            this.initBody();
        };

        BootstrapTable.prototype.getCaret = function() {
            var that = this;

            $.each(this.$header.find('th'), function(i, th) {
                var sortName = that.header.sortNames[i];
                $(th).find('.sortable').removeClass('desc asc').addClass((sortName || $(th).data('field')) === that.options.sortName ? that.options.sortOrder : 'both');
            });
        };
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
 * 加载form表单验证 <class = tonto-form-validate>
 */
function _initForm(container, formOptions) {


    /**
     * 与ajax-form-submit结合处理子窗口提交form后回调（例如关闭子窗口并刷新父窗口表格）
     */
    $.extend({
        setLayerSubmitHandler: function(layero, index, submitSuccess, msg) {
            var forms = layer.getChildFrame('form', index);
            if (forms && forms.length > 0) {
                forms.each(function() {
                    $(this)[0].submitSuccessHandler = function(data) {

                        if (typeof submitSuccess == 'string') {
                            msg = submitSuccess;
                            submitSuccess = null;
                        }

                        if (msg) {
                            $.successMessage(msg);
                        }
                        if (submitSuccess) {
                            submitSuccess(data);
                        }
                    };
                });
            }
        }
    });

    $.fn.setFormSubmitHandler = function(submitSuccess, msg) {
        var form = $(this);

        if (typeof submitSuccess == 'string') {
            msg = submitSuccess;
            submitSuccess = null;
        }

        form[0].submitSuccessHandler = function(data) {
            if (msg) {
                $.successMessage(msg);
            }
            if (submitSuccess) {
                submitSuccess(data);
            }
        };
    };

    var forms = container ? $(container).find(".tonto-form-validate") : $(".tonto-form-validate");

    forms.each(function() {
        var submitForm = $(this);

        var submitBtn = submitForm.find('button[type="submit"],input[type="submit"]')

        submitBtn.each(function() {
            var that = $(this);
            that.on('click', function(e) {
                if (that.data("loading")) {
                    return;
                }
                // ie处理placeholder提交问题
                if ($.browser && $.browser.msie) {
                    submitForm.find('[placeholder]').each(function() {
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
                $.each(errorList, function(i, v) {
                    // 在此处用了layer的方法,显示效果更美观
                    layer.tips(v.message, v.element, { time: 2000, tips: [3, 'red'] });
                    return false;
                });
            },
            submitHandler: function(a) {
                var form = $(a);
                form.ajaxSubmit({
                    url: submitBtn.data('action') ? submitBtn.data('action') : form.attr('action'),
                    dataType: 'json',
                    beforeSubmit: function(arr, $form, options) {
                        submitBtn.each(function() {
                            var that = $(this);
                            that.data("loading", true);
                            var text = that.text();
                            that.data("orginText", text);
                            that.text(text + '中...').prop('disabled', true).addClass('disabled');
                        });
                    },
                    success: function(data) {

                        submitBtn.each(function() {
                            var that = $(this);
                            var text = that.text();
                            that.removeClass('disabled').prop('disabled', false).text(that.data("orginText"));
                        });

                        var resStatus = data.status,
                            status = _RESPONSE_STATUS;

                        if (status.NO_LOGIN === resStatus) {
                            ajaxUnLoginHandler(form.data("submitSuccessHandler"));
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
                            var handler = form[0].submitSuccessHandler || form.data("submitSuccessHandler");
                            if (handler) {
                                handler(data.result ? data.result : data);
                            }
                        }
                    },
                    error: function(xhr, e, statusText) {
                        $.errorMessage("系统异常");
                    },
                    complete: function() {
                        submitBtn.data("loading", false);
                    }
                });
            }
        };

        if (formOptions)
            config = $.extend(config, formOptions);

        var backurl = submitForm.attr("callback-url");

        if (backurl && !(submitForm[0].submitSuccessHandler || submitForm.data("submitSuccessHandler"))) {
            submitForm.setFormSubmitHandler(function() {
                layer.alert("操作成功", function(index) {
                    layer.close(index);
                    window.location = backurl;
                });
            });
        }

        submitForm.createFormValidater(config);
    });
}


// ------------------------------------------
//
// 页面处理
//
// -----------------------------------------

/**
 * 信息描述通用显示
 */
function _showInfoDescription(items, container, isHorizontal) {

    var c = $(container);
    c.empty();
    var dl = $('<dl ' + (isHorizontal ? 'class="dl-horizontal"' : '') + '></dl>');

    items.forEach(function(i) {
        if(i.content instanceof jQuery){
            dl.append("<dt>" + i.title + ":</dt>");
            var k = $("<dd></dd>");
            k.append(i.content);
            dl.append(k);
        } else {
            dl.append("<dt>" + i.title + ":</dt><dd>" + i.content + "</dd>");
        }

    });

    c.append(dl);
}