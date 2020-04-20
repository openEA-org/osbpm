function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "CSS Tekst";
    txtLang[1].innerHTML = "Klasse Naam";
    
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "You're selecting BODY element.":
            return "You're selecting BODY element.";
        case "Please select a text.":
            return "Please select a text.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Eigen CSS</title>")
    }