var sStyleWeight1;
var sStyleWeight2;
var sStyleWeight3;
var sStyleWeight4; 

function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Schrift";
    txtLang[1].innerHTML = "Stil";
    txtLang[2].innerHTML = "Gr&ouml;&szlig;e";
    txtLang[3].innerHTML = "Schriftfarbe";
    txtLang[4].innerHTML = "Hintergrund";
    
    txtLang[5].innerHTML = "Hervorhebung";
    txtLang[6].innerHTML = "Text Case";
    txtLang[7].innerHTML = "Kapit&auml;lchen";
    txtLang[8].innerHTML = "Versatz";

    txtLang[9].innerHTML = "k.A.";
    txtLang[10].innerHTML = "Unterstrichen";
    txtLang[11].innerHTML = "&Uuml;berstrichen";
    txtLang[12].innerHTML = "Durchgestrichen";
    txtLang[13].innerHTML = "keine";

    txtLang[14].innerHTML = "k.A.";
    txtLang[15].innerHTML = "Gross/Klein";
    txtLang[16].innerHTML = "GROSS";
    txtLang[17].innerHTML = "klein";
    txtLang[18].innerHTML = "keine";

    txtLang[19].innerHTML = "k.A.";
    txtLang[20].innerHTML = "Kapit&auml;lchen";
    txtLang[21].innerHTML = "Normal";

    txtLang[22].innerHTML = "k.A.";
    txtLang[23].innerHTML = "hochgestellt";
    txtLang[24].innerHTML = "tiefgestellt";
    txtLang[25].innerHTML = "Relativ";      //added by PAS
    txtLang[26].innerHTML = "Grundlinie";   //added by PAS
    
    txtLang[27].innerHTML = "Spationierung";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Regular"
    optLang[1].text = "Kursiv"
    optLang[2].text = "Fett"
    optLang[3].text = "Fett Kursiv"
    
    optLang[0].value = "Regular"
    optLang[1].value = "Kursiv"
    optLang[2].value = "Fett"
    optLang[3].value = "Fett Kursiv"
    
    sStyleWeight1 = "Regular"
    sStyleWeight2 = "Kursiv"
    sStyleWeight3 = "Fett"
    sStyleWeight4 = "Fett Kursiv"
    
    optLang[4].text = "oben"
    optLang[5].text = "mitte"
    optLang[6].text = "unten"
    optLang[7].text = "Text-oben"
    optLang[8].text = "Text-unten"
    
    document.getElementById("btnPick1").value = "w\u00E4hlen";//"Pick";
    document.getElementById("btnPick2").value = "w\u00E4hlen";//"Pick";

    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Benutzerfarben";
        case "More Colors...": return "weitere Farben...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Textformatierung</title>")
    }