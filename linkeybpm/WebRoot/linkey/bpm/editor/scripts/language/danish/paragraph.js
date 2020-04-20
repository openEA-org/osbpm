function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Justering";
    txtLang[1].innerHTML = "Indryk";
    txtLang[2].innerHTML = "Afstand";
    txtLang[3].innerHTML = "Tegnafstand";
    txtLang[4].innerHTML = "Linieh&#248;jde";
    txtLang[5].innerHTML = "Dekoration";
    txtLang[6].innerHTML = "Mellemrum";
    
    document.getElementById("divPreview").innerHTML = "Lorem ipsum dolor sit amet, " +
        "consetetur sadipscing elitr, " +
        "sed diam nonumy eirmod tempor invidunt ut labore et " +
        "dolore magna aliquyam erat, " +
        "sed diam voluptua. At vero eos et accusam et justo " +
        "duo dolores et ea rebum. Stet clita kasd gubergren, " +
        "no sea takimata sanctus est Lorem ipsum dolor sit amet.";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "ingen";
    optLang[1].text = "Venstre";
    optLang[2].text = "H\u00F8jre"
    optLang[3].text = "Centrer";
    optLang[4].text = "Udj\u00E6vn"
    optLang[5].text = "Ingen";
    optLang[6].text = "Kapit\u00E6ler"
    optLang[7].text = "St. bogstv.";
    optLang[8].text = "Sm\u00E5 bogstv."
    optLang[9].text = "Standard";
    optLang[10].text = "Ingen";
    optLang[11].text = "Ingen linieskift";
    optLang[12].text = "pre";
    optLang[13].text = "Normal";
    
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";   
    }
function writeTitle()
    {
    document.write("<title>Typografi formatering</title>")
    }
