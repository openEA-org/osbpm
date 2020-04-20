function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Zeile einf&uuml;gen";
    txtLang[1].innerHTML = "Spalte einf&uuml;gen";
    txtLang[2].innerHTML = "Zeilen verbinden";
    txtLang[3].innerHTML = "Spalten verbinden";
    txtLang[4].innerHTML = "Zeile l&ouml;schen";
    txtLang[5].innerHTML = "Spalte l&ouml;schen";

	document.getElementById("btnInsRowAbove").title="Zeile einf\u00FCgen (Above)";
	document.getElementById("btnInsRowBelow").title="Zeile einf\u00FCgen (Below)";
	document.getElementById("btnInsColLeft").title="Spalte einf\u00FCgen (Left)";
	document.getElementById("btnInsColRight").title="Spalte einf\u00FCgen (Right)";
	document.getElementById("btnIncRowSpan").title="Increase Rowspan";
	document.getElementById("btnDecRowSpan").title="Decrease Rowspan";
	document.getElementById("btnIncColSpan").title="Increase Colspan";
	document.getElementById("btnDecColSpan").title="Decrease Colspan";
	document.getElementById("btnDelRow").title="Zeile l\u00F6schen";
	document.getElementById("btnDelCol").title="Spalte l\u00F6schen";

	document.getElementById("btnClose").value = " schlie\u00DFen ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Cannot delete column.":
            return "Die Kolumne kann nicht gel\u00F6scht werden\u002C da sie verbundene Zellen beinhaltet. Bitte entfernen Sie erst diese Verbindung.";
        case "Cannot delete row.":
            return "Die Zeile kann nicht gel\u00F6scht werden\u002C da sie verbundene Zellen beinhaltet. Bitte entfernen Sie erst diese Verbindung.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Gr&ouml;&szlig;e&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</title>")
    }