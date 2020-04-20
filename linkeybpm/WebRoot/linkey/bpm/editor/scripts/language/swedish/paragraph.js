function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Placering";
    txtLang[1].innerHTML = "Indrag";
    txtLang[2].innerHTML = "Ordmellanrum";
    txtLang[3].innerHTML = "Teckenmellanrum";
    txtLang[4].innerHTML = "Radavst\u00E5nd";
    txtLang[5].innerHTML = "Text";
    txtLang[6].innerHTML = "Tomt mellanrum";
    
    document.getElementById("divPreview").innerHTML = "Lorem ipsum dolor sit amet, " +
        "consetetur sadipscing elitr, " +
        "sed diam nonumy eirmod tempor invidunt ut labore et " +
        "dolore magna aliquyam erat, " +
        "sed diam voluptua. At vero eos et accusam et justo " +
        "duo dolores et ea rebum. Stet clita kasd gubergren, " +
        "no sea takimata sanctus est Lorem ipsum dolor sit amet.";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Ej valt";
    optLang[1].text = "V\u00E4nster";
    optLang[2].text = "H\u00F6ger";
    optLang[3].text = "Centrerad";
    optLang[4].text = "Marginaljusterad";
    optLang[5].text = "Ej valt";
    optLang[6].text = "Stor bokstav";
    optLang[7].text = "Versaler";
    optLang[8].text = "Gemener";
    optLang[9].text = "Ingen";
    optLang[10].text = "Ej valt";
    optLang[11].text = "Ingen brytning";
    optLang[12].text = "pre";
    optLang[13].text = "Normal";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";   
    }
function writeTitle()
    {
    document.write("<title>Formatmall</title>")
    }
