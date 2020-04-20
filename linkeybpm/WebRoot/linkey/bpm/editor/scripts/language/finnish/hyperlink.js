function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "L\u00E4hde";
    txtLang[1].innerHTML = "Kirjanmerkki";
    txtLang[2].innerHTML = "Kohde";
    txtLang[3].innerHTML = "Otsikko";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Kohdesivu"
    optLang[1].text = "Tyhj\u00E4"
    optLang[2].text = "Alasivu"
    
    document.getElementById("btnCancel").value = "Peruuta";
    document.getElementById("btnInsert").value = "Liit\u00E4";
    document.getElementById("btnApply").value = "K\u00E4yt\u00E4";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Hyperlinkki</title>")
    }