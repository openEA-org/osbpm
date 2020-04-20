function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "插入行";
    txtLang[1].innerHTML = "插入列";
    txtLang[2].innerHTML = "合并/拆分<br>行";
    txtLang[3].innerHTML = "合并/拆分<br>列";
    txtLang[4].innerHTML = "删除行";
    txtLang[5].innerHTML = "删除列";

	document.getElementById("btnInsRowAbove").title="Insert Row (Above)";
	document.getElementById("btnInsRowBelow").title="Insert Row (Below)";
	document.getElementById("btnInsColLeft").title="Insert Column (Left)";
	document.getElementById("btnInsColRight").title="Insert Column (Right)";
	document.getElementById("btnIncRowSpan").title="Increase Rowspan";
	document.getElementById("btnDecRowSpan").title="Decrease Rowspan";
	document.getElementById("btnIncColSpan").title="Increase Colspan";
	document.getElementById("btnDecColSpan").title="Decrease Colspan";
	document.getElementById("btnDelRow").title="Delete Row";
	document.getElementById("btnDelCol").title="Delete Column";
	document.getElementById("btnClose").value = " close ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Cannot delete column.":
            return "Cannot delete column. The column contains spanned cells from another column. Please remove the span first.";
        case "Cannot delete row.":
            return "Cannot delete row. The row contains spanned cells from another rows. Please remove the span first.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>修改表单</title>")
    }