function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Ausrichtung";
    txtLang[1].innerHTML = "Einr&uuml;cken";
    txtLang[2].innerHTML = "Wortabstand";
    txtLang[3].innerHTML = "Zeichenabstand";
    txtLang[4].innerHTML = "Zeilenh&ouml;he";
    txtLang[5].innerHTML = "Text Case";
    txtLang[6].innerHTML = "Umbruch";
    
    document.getElementById("divPreview").innerHTML = "Lorem ipsum dolor sit amet, " +
        "consetetur sadipscing elitr, " +
        "sed diam nonumy eirmod tempor invidunt ut labore et " +
        "dolore magna aliquyam erat, " +
        "sed diam voluptua. At vero eos et accusam et justo " +
        "duo dolores et ea rebum. Stet clita kasd gubergren, " +
        "no sea takimata sanctus est Lorem ipsum dolor sit amet.";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "k.A.";
    optLang[1].text = "Links";
    optLang[2].text = "Rechts";
    optLang[3].text = "Zentriert";
    optLang[4].text = "Blocksatz";
    optLang[5].text = "k.A.";
    optLang[6].text = "Gross/Klein";
    optLang[7].text = "GROSS";
    optLang[8].text = "klein";
    optLang[9].text = "keine";
    optLang[10].text = "k.A.";
    optLang[11].text = "kein Umbruch";
    optLang[12].text = "pre";
    optLang[13].text = "normal";
    
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Absatzformatierung</title>")
    }
