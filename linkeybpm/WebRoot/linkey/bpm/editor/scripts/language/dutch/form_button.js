function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Type";
    txtLang[1].innerHTML = "Naam";
    txtLang[2].innerHTML = "Waarde";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Knop"
    optLang[1].text = "Verzenden"
    optLang[2].text = "Herstel"
        
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnInsert").value = "invoegen";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Knop</title>")
    }