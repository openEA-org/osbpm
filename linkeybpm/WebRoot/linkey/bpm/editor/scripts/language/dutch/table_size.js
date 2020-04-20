function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Rij Invoegen";
    txtLang[1].innerHTML = "Kolom Invoegen";
    txtLang[2].innerHTML = "Rij Samenvoegen";
    txtLang[3].innerHTML = "Kolom Samenvoegen";
    txtLang[4].innerHTML = "Rij Verwijderen";
    txtLang[5].innerHTML = "Kolom Verwijderen";

	document.getElementById("btnInsRowAbove").title="Rij Invoegen (Above)";
	document.getElementById("btnInsRowBelow").title="Rij Invoegen (Below)";
	document.getElementById("btnInsColLeft").title="Kolom Invoegen (Left)";
	document.getElementById("btnInsColRight").title="Kolom Invoegen (Right)";
	document.getElementById("btnIncRowSpan").title="Increase Rowspan";
	document.getElementById("btnDecRowSpan").title="Decrease Rowspan";
	document.getElementById("btnIncColSpan").title="Increase Colspan";
	document.getElementById("btnDecColSpan").title="Decrease Colspan";
	document.getElementById("btnDelRow").title="Rij Verwijderen";
	document.getElementById("btnDelCol").title="Kolom Verwijderen";

	document.getElementById("btnClose").value = " sluiten ";
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
    document.write("<title>Grootte</title>")
    }