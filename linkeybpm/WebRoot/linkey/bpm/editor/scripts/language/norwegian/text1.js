var sStyleWeight1;
var sStyleWeight2;
var sStyleWeight3;
var sStyleWeight4; 

function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Skrifttype";
    txtLang[1].innerHTML = "Typografi";
    txtLang[2].innerHTML = "St\u00F8rrelse";
    txtLang[3].innerHTML = "Forgrunn";
    txtLang[4].innerHTML = "Bakgrunn";
    
    txtLang[5].innerHTML = "Dekorasjon";
    txtLang[6].innerHTML = "Tekst";
    txtLang[7].innerHTML = "Minicaps";
    txtLang[8].innerHTML = "Vertikal";

    txtLang[9].innerHTML = "Ingen";
    txtLang[10].innerHTML = "Understreket";
    txtLang[11].innerHTML = "Overstreket";
    txtLang[12].innerHTML = "Gjennomstreket";
    txtLang[13].innerHTML = "Normal";

    txtLang[14].innerHTML = "Ingen";
    txtLang[15].innerHTML = "Kapit\u00E6ler";
    txtLang[16].innerHTML = "St. bokstv.";
    txtLang[17].innerHTML = "Sm\u00E5 bokstv.";
    txtLang[18].innerHTML = "Normal";

    txtLang[19].innerHTML = "Ingen";
    txtLang[20].innerHTML = "Small-Caps";
    txtLang[21].innerHTML = "Normal";

    txtLang[22].innerHTML = "Ingen";
    txtLang[23].innerHTML = "Hevet skrift";
    txtLang[24].innerHTML = "Senket skrift";
    txtLang[25].innerHTML = "Relativ";
    txtLang[26].innerHTML = "Grundlinie";//Baseline
    
    txtLang[27].innerHTML = "Tegnafstand";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Almindlig"
    optLang[1].text = "Kursiv"
    optLang[2].text = "Fed"
    optLang[3].text = "Fed kursiv"
    
    optLang[0].value = "Almindelig"
    optLang[1].value = "Kursiv"
    optLang[2].value = "Fed"
    optLang[3].value = "Fed kursiv"
    
    sStyleWeight1 = "Almindelig"
    sStyleWeight2 = "Kursiv"
    sStyleWeight3 = "Fed"
    sStyleWeight4 = "Fed kursiv"
    
    optLang[4].text = "\u00D8verst"
    optLang[5].text = "Midt"
    optLang[6].text = "Nederst"
    optLang[7].text = "Tekst top"
    optLang[8].text = "Tekst bund"
    
    document.getElementById("btnPick1").value = "V\u00E6lg"
    document.getElementById("btnPick2").value = "V\u00E6lg"

    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Tekst formatering</title>")
    }