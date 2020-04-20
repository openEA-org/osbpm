function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "K\u00E4lla";
    txtLang[1].innerHTML = "Alternativ text";
    txtLang[2].innerHTML = "Mellanrum";
    txtLang[3].innerHTML = "Placering";
    txtLang[4].innerHTML = "\u00D6verst";
    txtLang[5].innerHTML = "Bildram";
    txtLang[6].innerHTML = "Nederst";
    txtLang[7].innerHTML = "Bredd";
    txtLang[8].innerHTML = "V\u00E4nster";
    txtLang[9].innerHTML = "H\u00F6jd";
    txtLang[10].innerHTML = "H\u00F6ger";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Abs. nederst";
    optLang[1].text = "Abs. mitten";
    optLang[2].text = "Baslinje";
    optLang[3].text = "Nederst";
    optLang[4].text = "V\u00E4nster";
    optLang[5].text = "Mitten";
    optLang[6].text = "H\u00F6ger";
    optLang[7].text = "Text-topp";
    optLang[8].text = "\u00D6verst";
 
    document.getElementById("btnBorder").value = " Kantlinje ";
    document.getElementById("btnReset").value = "\u00C5terst\u00E4ll";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Infoga";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Bild</title>")
    }