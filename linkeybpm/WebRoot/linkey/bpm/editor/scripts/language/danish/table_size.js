function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Inds\u00E6t r\u00E6kke";
    txtLang[1].innerHTML = "Inds\u00E6t kolonne";
    txtLang[2].innerHTML = "Flet r\u00E6kke";
    txtLang[3].innerHTML = "Flet kolonne";
    txtLang[4].innerHTML = "Slet r\u00E6kke";
    txtLang[5].innerHTML = "Slet kolonne";

	document.getElementById("btnInsRowAbove").title="Inds\u00E6t r\u00E6kke (Over)";
	document.getElementById("btnInsRowBelow").title="Inds\u00E6t r\u00E6kke (Under)";
	document.getElementById("btnInsColLeft").title="Inds\u00E6t kolonne (Venstre)";
	document.getElementById("btnInsColRight").title="Inds\u00E6t kolonne (H\u00F8jre)";
	document.getElementById("btnIncRowSpan").title="For\u00F8g R\u00E6kkespan";
	document.getElementById("btnDecRowSpan").title="Reduc\u00E9r R\u00E6kkespan";
	document.getElementById("btnIncColSpan").title="For\u00F8g Kolonnespan";
	document.getElementById("btnDecColSpan").title="Reduc\u00E9r Kolonnespan";
	document.getElementById("btnDelRow").title="Slet r\u00E6kke";
	document.getElementById("btnDelCol").title="Slet kolonne";

	document.getElementById("btnClose").value = " Luk ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Cannot delete column.":
            return "Kolonnen kan ikke slettes. Kolonnen indeholder flettet celler fra andre kolonner, fjern venligst de flette celler f\u00F8rst.";
        case "Cannot delete row.":
            return "R\u00E6kken kan ikke slettes. R\u00E6kken indeholder flettede celler fra andre r\u00E6kker, fjern venligst dem f\u00F8rst.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Celler</title>")
    }