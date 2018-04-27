$(function() {
    $.getAjax("/health/index/item/list", initItemTree);
});

var g = function(nodeItems, data) {
    var nodes = [];

    if ($.isArray(nodeItems)) {
        nodeItems.forEach(function(item) {

            var text = item.name;

            if (item.itemType == 'STANDARD') {
                text += '<span class="label label-warning pull-right">标准</span>';
            } else {
                //text += '<span class="label label-warning pull-right">分类</span>';
            }

            itemMap[item.id] = item;

            var node = {
                text: text,
                data: item
            };

            var children = $.grep(data, function(row, index) {
                return item.id == row.parentId;
            });

            if (children && children.length > 0) {
                node.nodes = g(children, data);
            }

            nodes.push(node);
        })
    }
    return nodes;
}

var tree = $('#tree');
var itemMap = {};

function initItemTree(result) {

    var itemList = result;

    var roots = $.grep(itemList, function(row, index) {
        return row.parentId == null;
    });

    var treedata = g(roots, itemList);


    tree.treeview({ data: treedata });
    tree.on('nodeSelected', function(event, node) {
        checkItem(node.data, true);
    });
}

function checkItem(item, selected) {
    if (typeof item != 'object') {
        item = itemMap[item];
    }
    getItemDetail(item, showItemDetail);

    if (!selected) {
        var t = tree.treeview(true);
        t.unselectNode(t.getSelected());
    }
}

function getItemDetail(item, callback) {

    var detail = $.getCache("item_detail_" + item.id);

    if (!detail) {
        $.getAjax("/health/index/item/detail?itemId=" + item.id, function(result) {

            var d = {
                valueDefinition: result.itemValueDefinition,
                dependenceDetail: result.itemDependenceDetail,
                standard: result.itemStandard
            };

            $.putCache("item_detail_" + item.id, d);

            if (typeof callback === 'function') {
                callback($.extend(item, d));
            }
        });

    } else {
        if (typeof callback === 'function') {
            callback($.extend(item, detail));
        }
    }
}

function showItemDetail(item) {
    var d = $("#item");
    var contents = [{
            title: "项目名称",
            content: item.name
        },
        {
            title: "类型",
            content: item.itemType
        }, {
            title: "KEY",
            content: item.itemKey
        }
    ];

    if (item.valueDefinition) {
        var vd = item.valueDefinition;
        if (vd.inputType == 'INPUT') {
            var content = templateContent(vd);

            contents.push({
                title: "值定义",
                content: content
            });

        } else if (vd.inputType == 'SELECT') {

            contents.push({
                title: "是否单选",
                content: vd.isSingle ? "是" : "否"
            });

            var content = "";

            if (item.standard) {
                item.standard.forEach(function(s) {
                    content += '<span class="label label-success" style="margin-right:10px">' + s.name + '</span>';
                });
            }

            contents.push({
                title: "选项",
                content: content
            });
        }

        if (item.dependenceDetail) {

            if (item.dependenceDetail.length == 1) {
                contents.push({
                    title: "依赖",
                    content: dependenceContent(item.dependenceDetail[0])
                });
            } else {

                var dh = "";

                item.dependenceDetail.forEach(function(i) {
                    dh += "<li>" + dependenceContent(i) + "</li>";
                });

                contents.push({
                    title: "依赖",
                    content: "<ul>" + dh + "</ul>"
                });
            }
        }

        d.showInfoDescription(contents, true);
    }
}

function dependenceContent(dependenceDetail) {

    var di = dependenceDetail.dependenceItem;
    var dd = dependenceDetail.itemDependence;
    var ds = dependenceDetail.dependenceStandard;
    var dv = dependenceDetail.dependenceValueDefinition;

    if (dd.dependenceRelation == 'KEY_EQUAL') {
        return wrapItemHref(di) + " <code>等于</code> " + '<span class="label label-success">' + (ds && ds.length > 0 ? ds[0].name : '') + "</span>";
    } else if (dd.dependenceRelation == 'KEY_NOT_EQUAL') {
        return wrapItemHref(di) + " <code>不等于</code> " + '<span class="label label-success">' + (ds && ds.length > 0 ? ds[0].name : '') + "</span>";
    } else if (dd.dependenceRelation == 'KEY_IN') {
        var x = "";
        if (ds && ds.length > 0) {
            ds.forEach(function(y) {
                x += '<span class="label label-success" style="margin-right:10px">' + y.name + '</span>';
            });
            x.length = x.length - 1;
        }
        x += "";

        return wrapItemHref(di) + " <code>包含于</code> " + x;
    } else if (dd.dependenceRelation == 'STRING_EQUAL') {
        return wrapItemHref(di) + " <code>等于</code> " + templateContent(dv, dd.dependenceValue);
    } else if (dd.dependenceRelation == 'STRING_NOT_EQUAL') {
        return wrapItemHref(di) + " <code>不等于</code> " + templateContent(dv, dd.dependenceValue);
    }

}

function templateContent(definition, value) {
    value = value ? "<b>" + value + "</b>" : "_______";

    var content = definition.template ? definition.template.replace("{?}", value) : value;

    if (definition.unit) {
        content += definition.unit;
    }

    return content;
}

function wrapItemHref(item) {
    return '<a href="javascript:checkItem(' + item.id + ')">' + item.name + '</a>';
}
