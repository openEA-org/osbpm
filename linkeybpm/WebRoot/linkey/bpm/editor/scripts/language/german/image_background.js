function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Bildpfad";
    txtLang[1].innerHTML = "Wiederholen";
    txtLang[2].innerHTML = "horizont. Ausrichtung";
    txtLang[3].innerHTML = "vertikale Ausrichtung";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "wiederholen"
    optLang[1].text = "nicht wiederholen"
    optLang[2].text = "horizontal wiederholen"
    optLang[3].text = "vertikal wiederholen"
    optLang[4].text = "links"
    optLang[5].text = "zentriert"
    optLang[6].text = "rechts"
    optLang[7].text = "Pixel"
    optLang[8].text = "Prozent"
    optLang[9].text = "oben"
    optLang[10].text = "mitte"
    optLang[11].text = "unten"
    optLang[12].text = "Pixel"
    optLang[13].text = "Prozent"
    
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Hintergrundbild</title>")
    }

