function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Tyyppi";
    txtLang[1].innerHTML = "Nimi";
    txtLang[2].innerHTML = "Koko";
    txtLang[3].innerHTML = "Maksimipituus";
    txtLang[4].innerHTML = "Rivien m\u00E4\u00E4r\u00E4";
    txtLang[5].innerHTML = "Arvo";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Teksti"
    optLang[1].text = "Tekstikentt\u00E4"
    optLang[2].text = "Salasana"
    
    document.getElementById("btnCancel").value = "Peruuta";
    document.getElementById("btnInsert").value = "Liit\u00E4";
    document.getElementById("btnApply").value = "K\u00E4yt\u00E4";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>"+"Tekstikentt\u00E4"+"</title>")
    }