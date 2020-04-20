function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Type";
    txtLang[1].innerHTML = "Naam";
    txtLang[2].innerHTML = "Grootte";
    txtLang[3].innerHTML = "Max Lengte";
    txtLang[4].innerHTML = "Aant Lijnen";
    txtLang[5].innerHTML = "Waarde";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Text"
    optLang[1].text = "Textgebied"
    optLang[2].text = "Wachtwoord"
    
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnInsert").value = "invoegen";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Tekst Veld</title>")
    }