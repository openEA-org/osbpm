function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Tyyppi";
    txtLang[1].innerHTML = "Nimi";
    txtLang[2].innerHTML = "Arvo";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Painike";
    optLang[1].text = "Hyv\u00E4ksy";
    optLang[2].text = "Tyhjenn\u00E4";
        
    document.getElementById("btnCancel").value = "Peruuta";
    document.getElementById("btnInsert").value = "Liit\u00E4";
    document.getElementById("btnApply").value = "K\u00E4yt\u00E4";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Painike</title>")
    }