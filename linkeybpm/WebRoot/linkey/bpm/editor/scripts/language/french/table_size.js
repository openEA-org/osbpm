function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Ins\u00E9rer une ligne";
    txtLang[1].innerHTML = "Ins\u00E9rer une colonne";
    txtLang[2].innerHTML = "Combiner la colonne";
    txtLang[3].innerHTML = "Combiner la ligne";
    txtLang[4].innerHTML = "Effacer la ligne";
    txtLang[5].innerHTML = "Effacer la colonne";

	document.getElementById("btnInsRowAbove").title="Ins\u00E9rer une ligne (Above)";
	document.getElementById("btnInsRowBelow").title="Ins\u00E9rer une ligne (Below)";
	document.getElementById("btnInsColLeft").title="Ins\u00E9rer une colonne (Left)";
	document.getElementById("btnInsColRight").title="Ins\u00E9rer une colonne (Right)";
	document.getElementById("btnIncRowSpan").title="Increase Rowspan";
	document.getElementById("btnDecRowSpan").title="Decrease Rowspan";
	document.getElementById("btnIncColSpan").title="Increase Colspan";
	document.getElementById("btnDecColSpan").title="Decrease Colspan";
	document.getElementById("btnDelRow").title="Effacer la ligne";
	document.getElementById("btnDelCol").title="Effacer la colonne";

	document.getElementById("btnClose").value = " Fermer ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Cannot delete column.":
            return "Ne pas supprimer la colonne. La colonne contient des cellules \u00E9tendues provenant d\u0027autres colonnes. Commencer à supprimer l\u0027extention en premier.";
        case "Cannot delete row.":
            return "Ne pas supprimer la ligne. La ligne contient des cellules \u00E9tendues provenant d\u0027autres lignes. Commencer à supprimer l\u0027extention en premier.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Taille</title>")
    }