function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Infoga rad";
    txtLang[1].innerHTML = "Infoga kolumn";
    txtLang[2].innerHTML = "Sammanfoga rad";
    txtLang[3].innerHTML = "Sammanfoga kolumn";
    txtLang[4].innerHTML = "Ta bort rad";
    txtLang[5].innerHTML = "Ta bort kolumn";

	document.getElementById("btnInsRowAbove").title="Infoga rad (Above)";
	document.getElementById("btnInsRowBelow").title="Infoga rad (Below)";
	document.getElementById("btnInsColLeft").title="Infoga kolumn (Left)";
	document.getElementById("btnInsColRight").title="Infoga kolumn (Right)";
	document.getElementById("btnIncRowSpan").title="Increase Rowspan";
	document.getElementById("btnDecRowSpan").title="Decrease Rowspan";
	document.getElementById("btnIncColSpan").title="Increase Colspan";
	document.getElementById("btnDecColSpan").title="Decrease Colspan";
	document.getElementById("btnDelRow").title="Ta bort rad";
	document.getElementById("btnDelCol").title="Ta bort kolumn";

	document.getElementById("btnClose").value = " St\u00E4ng ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Cannot delete column.":
            return "Kan inte ta bort kolumn. Kolumnen inneh\u00E5ller sammanfogade celler fr\u00E5n en annan kolumn. V\u00E4nligen ta bort sammanfogningen f\u00F6rst.";
        case "Cannot delete row.":
            return "Kan inte ta bort rad. Raden inneh\u00E5ller sammanfogade celler fr\u00E5n en annan rad. V\u00E4nligen ta bort den sammanfogningen f\u00F6rst.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Storlek&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</title>")
    }