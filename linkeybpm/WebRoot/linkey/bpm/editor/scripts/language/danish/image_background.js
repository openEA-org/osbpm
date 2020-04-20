function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sti";
    txtLang[1].innerHTML = "Gentag";
    txtLang[2].innerHTML = "Vandret justering";
    txtLang[3].innerHTML = "Lodret justering";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Gentag"
    optLang[1].text = "Ikke gentaget"
    optLang[2].text = "Gentag X"
    optLang[3].text = "Gentag Y"
    optLang[4].text = "Venstre"
    optLang[5].text = "Centrer"
    optLang[6].text = "H\u00F8jre"
    optLang[7].text = "pixels"
    optLang[8].text = "procent"
    optLang[9].text = "\u00D8verst"
    optLang[10].text = "Midt"
    optLang[11].text = "Nedesrt"
    optLang[12].text = "pixels"
    optLang[13].text = "procent"
    
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Baggrundsbillede</title>")
    }