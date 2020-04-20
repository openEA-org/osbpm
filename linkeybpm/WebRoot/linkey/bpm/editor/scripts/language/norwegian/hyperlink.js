function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sti";
    txtLang[1].innerHTML = "Bokmerke";
    txtLang[2].innerHTML = "Ramme";
    txtLang[3].innerHTML = "Tittel";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Samme"
    optLang[1].text = "Nytt vindu"
    optLang[2].text = "Overordnet"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Sett inn";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Link</title>")
    }