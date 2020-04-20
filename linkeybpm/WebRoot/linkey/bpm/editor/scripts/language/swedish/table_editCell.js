function loadTxt()
    {    
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Autopassa";
    txtLang[1].innerHTML = "Egenskaper";
    txtLang[2].innerHTML = "Format";
    txtLang[3].innerHTML = "Bredd";
    txtLang[4].innerHTML = "Autopassa efter inneh\u00E5llet";
    txtLang[5].innerHTML = "Fast cellbredd";
    txtLang[6].innerHTML = "H\u00F6jd";
    txtLang[7].innerHTML = "Autopassa efter inneh\u00E5llet";
    txtLang[8].innerHTML = "Fast cellh\u00F6jd";
    txtLang[9].innerHTML = "Textorientering";
    txtLang[10].innerHTML = "Utfyllnad";
    txtLang[11].innerHTML = "V\u00E4nster";
    txtLang[12].innerHTML = "H\u00F6ger";
    txtLang[13].innerHTML = "\u00D6verst";
    txtLang[14].innerHTML = "Nederst";
    txtLang[15].innerHTML = "Tomt mellanrum";
    txtLang[16].innerHTML = "Bakgrund";
    txtLang[17].innerHTML = "F\u00F6rhandsgranska";
    txtLang[18].innerHTML = "Formatering";
    txtLang[19].innerHTML = "Applicera p\u00E5";

    document.getElementById("btnPick").value = "V\u00E4lj";
    document.getElementById("btnImage").value = "Bild";
    document.getElementById("btnText").value = " Textformatering ";
    document.getElementById("btnBorder").value = " Kantlinje ";

    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixlar"
    optLang[1].text = "procent"
    optLang[2].text = "pixlar"
    optLang[3].text = "procent"
    optLang[4].text = "ingen"
    optLang[5].text = "\u00F6verst"
    optLang[6].text = "mitten"
    optLang[7].text = "nederst"
    optLang[8].text = "baslinje"
    optLang[9].text = "neds\u00E4nkt"
    optLang[10].text = "upph\u00F6jd"
    optLang[11].text = "text-\u00F6verst"
    optLang[12].text = "text-nederst"
    optLang[13].text = "ingen"
    optLang[14].text = "v\u00E4nster"
    optLang[15].text = "centrerad"
    optLang[16].text = "h\u00F6ger"
    optLang[17].text = "justerad"
    optLang[18].text = "Ej valt"
    optLang[19].text = "Ingen brytning"
    optLang[20].text = "pre"
    optLang[21].text = "Normal"
    optLang[22].text = "Aktuell cell"
    optLang[23].text = "Aktuell rad"
    optLang[24].text = "Aktuell kolumn"
    optLang[25].text = "Whole Table"
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
    document.write("<title>Cellegenskaper</title>")
    }