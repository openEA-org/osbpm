function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Typografi";
    txtLang[1].innerHTML = "Eksempel";
    txtLang[2].innerHTML = "Overf\u00F8r til";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Valgte takst"
    optLang[1].text = "Aktuell kodetag"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "You're selecting BODY element.":
            return "Du kan ikke formatere BODY elementet.";
        case "Please select a text.":
            return "Det skal markeres en tekst f\u00F8r oppdatering kan skje.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Typografi</title>")
    }