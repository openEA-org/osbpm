function loadTxt()
	{
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "S\u00F8g";
    txtLang[1].innerHTML = "Erstat";
    txtLang[2].innerHTML = "Match store/sm\u00EA ord";
    txtLang[3].innerHTML = "Match hele ord";
    
    document.getElementById("btnSearch").value = "s\u00F8g n\u00E6ste";;
    document.getElementById("btnReplace").value = "erstat";
    document.getElementById("btnReplaceAll").value = "erstat alle";  
    document.getElementById("btnClose").value = "luk";
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
	document.write("<title>S\u00F8g & Erstat</title>")
	}