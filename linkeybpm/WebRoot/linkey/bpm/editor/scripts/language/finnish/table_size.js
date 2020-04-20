function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Lis\u00E4\u00E4 rivi";
    txtLang[1].innerHTML = "Lis\u00E4\u00E4 sarake";
    txtLang[2].innerHTML = "Yhdist\u00E4 rivi";
    txtLang[3].innerHTML = "Yhdist\u00E4 sarake";
    txtLang[4].innerHTML = "Poista rivi";
    txtLang[5].innerHTML = "Poista sarake";

	document.getElementById("btnInsRowAbove").title="Lis\u00E4\u00E4 rivi (Above)";
	document.getElementById("btnInsRowBelow").title="Lis\u00E4\u00E4 rivi (Below)";
	document.getElementById("btnInsColLeft").title="Lis\u00E4\u00E4 sarake (Left)";
	document.getElementById("btnInsColRight").title="Lis\u00E4\u00E4 sarake (Right)";
	document.getElementById("btnIncRowSpan").title="Increase Rowspan";
	document.getElementById("btnDecRowSpan").title="Decrease Rowspan";
	document.getElementById("btnIncColSpan").title="Increase Colspan";
	document.getElementById("btnDecColSpan").title="Decrease Colspan";
	document.getElementById("btnDelRow").title="Poista rivi";
	document.getElementById("btnDelCol").title="Poista sarake";

	document.getElementById("btnClose").value = " Sulje ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Cannot delete column.":
            return "Saraketta ei voi poistaa. Sarake sis\u00E4lt\u00E4\u00E4 yhdistettyj\u00E4 soluja.";
        case "Cannot delete row.":
            return "Rivi\u00E4 ei voi poistaa. Rivi sis\u00E4lt\u00E4\u00E4 yhdistettyj\u00E4 soluja.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Koko</title>")
    }