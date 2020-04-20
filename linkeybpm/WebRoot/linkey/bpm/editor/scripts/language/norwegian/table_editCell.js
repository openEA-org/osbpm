function loadTxt()
    {    
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "St\u00F8rrelse";
    txtLang[1].innerHTML = "Egenskaper";
    txtLang[2].innerHTML = "Typografi";
    txtLang[3].innerHTML = "Bredde";
    txtLang[4].innerHTML = "Bredde styres av innhold";
    txtLang[5].innerHTML = "Bredde";
    txtLang[6].innerHTML = "H\u00F8yde";
    txtLang[7].innerHTML = "Bredde styres av innhold";
    txtLang[8].innerHTML = "H\u00F8yde";
    txtLang[9].innerHTML = "Loddrett justering";
    txtLang[10].innerHTML = "Margen";
    txtLang[11].innerHTML = "Venstre";
    txtLang[12].innerHTML = "H\u00F8yre";
    txtLang[13].innerHTML = "Topp";
    txtLang[14].innerHTML = "Nederst";
    txtLang[15].innerHTML = "Mellomrom";
    txtLang[16].innerHTML = "Bakgrunn";
    txtLang[17].innerHTML = "Eksempel";
    txtLang[18].innerHTML = "Typografi";
    txtLang[19].innerHTML = "Overf\u00F8r til";

    document.getElementById("btnPick").value = "Velg";
    document.getElementById("btnImage").value = "Bilde";
    document.getElementById("btnText").value = " Tekst typografi ";
    document.getElementById("btnBorder").value = " Ramme typografi ";

    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels"
    optLang[1].text = "prosent"
    optLang[2].text = "pixels"
    optLang[3].text = "prosent"
    optLang[4].text = "Standard"
    optLang[5].text = "\u00D8verst"
    optLang[6].text = "Midten"
    optLang[7].text = "Nederst"
    optLang[8].text = "Grunnlinje"
    optLang[9].text = "Senket"
    optLang[10].text = "Hevet"
    optLang[11].text = "Tekst topp"
    optLang[12].text = "Tekst nederst"
    optLang[13].text = "Standard"
    optLang[14].text = "Venstre"
    optLang[15].text = "Midtstill"
    optLang[16].text = "H\u00F8yre"
    optLang[17].text = "Utjevn"
    optLang[18].text = "Ingen"
    optLang[19].text = "Ingen linjeskift"
    optLang[20].text = "Pre"
    optLang[21].text = "Normal"
    optLang[22].text = "Aktuell celle"
    optLang[23].text = "Aktuell rad"
    optLang[24].text = "Aktuell kolonne"
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
    document.write("<title>Celle egenskaper</title>")
    }