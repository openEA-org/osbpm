function loadTxt()
	{
    	var txtLang = document.getElementsByName("txtLang");
    	txtLang[0].innerHTML = "Suchen";
    	txtLang[1].innerHTML = "Ersetzen";
    	txtLang[2].innerHTML = "GROSS/Klein beachten";
    	txtLang[3].innerHTML = "nur ganze W\u00f6rter";
    
    	document.getElementById("btnSearch").value = "weitersuchen";;
    	document.getElementById("btnReplace").value = "ersetzen";
    	document.getElementById("btnReplaceAll").value = "alles ersetzen";  
    	document.getElementById("btnClose").value = "schlie\u00DFen";
	}
function getTxt(s)
    {
    switch(s)
        {
        case "Finished searching": return "Finished searching the document.\nSearch again from the top?";
        default: return "";
        }
    }
function writeTitle()
	{
	document.write("<title>Suchen & Ersetzen</title>")
	}