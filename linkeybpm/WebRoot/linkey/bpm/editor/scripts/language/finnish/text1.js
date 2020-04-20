var sStyleWeight1;
var sStyleWeight2;
var sStyleWeight3;
var sStyleWeight4; 

function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Teckensnitt";
    txtLang[1].innerHTML = "Format";
    txtLang[2].innerHTML = "Storlek";
    txtLang[3].innerHTML = "F\u00F6rgrund";
    txtLang[4].innerHTML = "Bakgrund";
    
    txtLang[5].innerHTML = "M\u00F6nster";
    txtLang[6].innerHTML = "Effekter";
    txtLang[7].innerHTML = "Liten bokstav";
    txtLang[8].innerHTML = "Vertikal";

    txtLang[9].innerHTML = "Ej valt";
    txtLang[10].innerHTML = "Understruken";
    txtLang[11].innerHTML = "\u00D6verstruken";
    txtLang[12].innerHTML = "Genomstruken";
    txtLang[13].innerHTML = "Ingen";

    txtLang[14].innerHTML = "Ej valt";
    txtLang[15].innerHTML = "Stor";
    txtLang[16].innerHTML = "Versaler";
    txtLang[17].innerHTML = "Gemener";
    txtLang[18].innerHTML = "Ingen";

    txtLang[19].innerHTML = "Ej valt";
    txtLang[20].innerHTML = "Kapit\u00E4ler";
    txtLang[21].innerHTML = "Normal";

    txtLang[22].innerHTML = "Ej valt";
    txtLang[23].innerHTML = "Upph\u00F6jd";
    txtLang[24].innerHTML = "Neds\u00E4nkt";
    txtLang[25].innerHTML = "Relativ";
    txtLang[26].innerHTML = "Baslinje";
    
    txtLang[27].innerHTML = "Teckenavst\u00E5nd";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Normal"
    optLang[1].text = "Kursiv"
    optLang[2].text = "Fet"
    optLang[3].text = "Fet Kursiv"
    
    optLang[0].value = "Normal"
    optLang[1].value = "Kursiv"
    optLang[2].value = "Fet"
    optLang[3].value = "Fet Kursiv"
    
    sStyleWeight1 = "Normal"
    sStyleWeight2 = "Krusiv"
    sStyleWeight3 = "Fet"
    sStyleWeight4 = "Fet Kurisv"
    
    optLang[4].text = "\u00D6verst"
    optLang[5].text = "Mitten"
    optLang[6].text = "Nederst"
    optLang[7].text = "Text-\u00F6verst"
    optLang[8].text = "Text-nederst"
    
    document.getElementById("btnPick1").value = "V\u00E4lj";
    document.getElementById("btnPick2").value = "V\u00E4lj";

    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
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
    document.write("<title>Textformatering</title>")
    }