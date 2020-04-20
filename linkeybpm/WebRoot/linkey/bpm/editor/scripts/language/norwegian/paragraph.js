function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Justering";
    txtLang[1].innerHTML = "Innrykk";
    txtLang[2].innerHTML = "Avstand";
    txtLang[3].innerHTML = "Tegnavstand";
    txtLang[4].innerHTML = "Linjeh&#248;yde";
    txtLang[5].innerHTML = "Dekorasjon";
    txtLang[6].innerHTML = "Mellomrom"
    
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
    optLang[2].text = "H\u00F8yre"
    optLang[3].text = "Midtstill";
    optLang[4].text = "Jevn ut"
    optLang[5].text = "Ingen";
    optLang[6].text = "Kapit\u00E6ler"
    optLang[7].text = "St. bokstv.";
    optLang[8].text = "Sm\u00E5 bokstv."
    optLang[9].text = "Standard";
    optLang[10].text = "Ingen";
    optLang[11].text = "Ingen linjeskift";
    optLang[12].text = "pre";
    optLang[13].text = "Normal";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";   
    }
function writeTitle()
    {
    document.write("<title>Typografi formatering</title>")
    }