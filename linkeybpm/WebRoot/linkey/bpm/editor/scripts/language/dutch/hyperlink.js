function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Source";
    txtLang[1].innerHTML = "Bladwijzer";
    txtLang[2].innerHTML = "Doel";
    txtLang[3].innerHTML = "Titel";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Zelfde frame"
    optLang[1].text = "Nieuw venster"
    optLang[2].text = "Bovenliggend frame"
    
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnInsert").value = "invoegen";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Hyperlink</title>")
    }