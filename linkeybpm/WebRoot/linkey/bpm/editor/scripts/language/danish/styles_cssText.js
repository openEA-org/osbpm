function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Typografi";
    txtLang[1].innerHTML = "Class navn";
    
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "You're selecting BODY element.":
            return "Du kan ikke formatere BODY elementet.";
        case "Please select a text.":
            return "Der skal markeres en tekst f\u00F8r opdatering kan ske.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Egne typografier</title>")
    }