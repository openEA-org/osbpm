function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "AutoAnpassen";
    txtLang[1].innerHTML = "Eigenschaften";
    txtLang[2].innerHTML = "Stil";
    txtLang[3].innerHTML = "Breite";
    txtLang[4].innerHTML = "AutoAnpassen an Inhalt";
    txtLang[5].innerHTML = "Feste Zellenbreite";
    txtLang[6].innerHTML = "H&ouml;he";
    txtLang[7].innerHTML = "AutoAnpassen an Inhalt";
    txtLang[8].innerHTML = "Feste Zellenh&ouml;he";
    txtLang[9].innerHTML = "Textausrichtung";
    txtLang[10].innerHTML = "Textabstand";
    txtLang[11].innerHTML = "links";
    txtLang[12].innerHTML = "rechts";
    txtLang[13].innerHTML = "oben";
    txtLang[14].innerHTML = "unten";
    txtLang[15].innerHTML = "Textumbruch";
    txtLang[16].innerHTML = "Hintergrund";
    txtLang[17].innerHTML = "Vorschau";
    txtLang[18].innerHTML = "CSS Text";
    txtLang[19].innerHTML = "anwenden auf";

    document.getElementById("btnPick").value = "w\u00E4hlen";
    document.getElementById("btnImage").value = "Bild";
    document.getElementById("btnText").value = " Textformatierung ";
    document.getElementById("btnBorder").value = " Rahmenstil ";

    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Pixel"
    optLang[1].text = "Prozent"
    optLang[2].text = "Pixel"
    optLang[3].text = "Prozent"
    optLang[4].text = "k.A."
    optLang[5].text = "oben"
    optLang[6].text = "mitte"
    optLang[7].text = "unten"
    optLang[8].text = "Grundlinie"
    optLang[9].text = "tief"
    optLang[10].text = "hoch"
    optLang[11].text = "Text oben"
    optLang[12].text = "Text unten"
    optLang[13].text = "k.A."
    optLang[14].text = "links"
    optLang[15].text = "zentriert"
    optLang[16].text = "rechts"
    optLang[17].text = "Blocksatz"
    optLang[18].text = "k.A."
    optLang[19].text = "kein Umbruch"
    optLang[20].text = "pre"
    optLang[21].text = "Normal"
    optLang[22].text = "aktuelle Zelle"
    optLang[23].text = "aktuelle Zeile"
    optLang[24].text = "aktuelle Spalte"
    optLang[25].text = "Whole Table"
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
    document.write("<title>Zelle Eigenschaften</title>")
    }