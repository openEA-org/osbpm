function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sett inn rad";
    txtLang[1].innerHTML = "Sett inn kolonne";
    txtLang[2].innerHTML = "Flett rad";
    txtLang[3].innerHTML = "Flett kolonne";
    txtLang[4].innerHTML = "Slett rad";
    txtLang[5].innerHTML = "Slett kolonne";

	document.getElementById("btnInsRowAbove").title="Sett inn rad (Above)";
	document.getElementById("btnInsRowBelow").title="Sett inn rad (Below)";
	document.getElementById("btnInsColLeft").title="Sett inn kolonne (Left)";
	document.getElementById("btnInsColRight").title="Sett inn kolonne (Right)";
	document.getElementById("btnIncRowSpan").title="Increase Rowspan";
	document.getElementById("btnDecRowSpan").title="Decrease Rowspan";
	document.getElementById("btnIncColSpan").title="Increase Colspan";
	document.getElementById("btnDecColSpan").title="Decrease Colspan";
	document.getElementById("btnDelRow").title="Slett rad";
	document.getElementById("btnDelCol").title="Slett kolonne";

	document.getElementById("btnClose").value = " Lukk ";
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
    document.write("<title>Celler&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</title>")
    }