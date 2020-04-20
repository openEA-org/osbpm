var sStyleWeight1;
var sStyleWeight2;
var sStyleWeight3;
var sStyleWeight4; 

function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Lettertype";
    txtLang[1].innerHTML = "Tekenstijl";
    txtLang[2].innerHTML = "Dikte";
    txtLang[3].innerHTML = "Voorgrond";
    txtLang[4].innerHTML = "Achtergrond";
    
    txtLang[5].innerHTML = "Decoratie";
    txtLang[6].innerHTML = "Tekst Stand";
    txtLang[7].innerHTML = "Minicaps";
    txtLang[8].innerHTML = "Verticaal";

    txtLang[9].innerHTML = "Niet gebruikt";
    txtLang[10].innerHTML = "Onderstrepen";
    txtLang[11].innerHTML = "Lijn boven";
    txtLang[12].innerHTML = "Doorhalen";
    txtLang[13].innerHTML = "Geen";

    txtLang[14].innerHTML = "Niet gebruikt";
    txtLang[15].innerHTML = "Kapitaal";
    txtLang[16].innerHTML = "Hoofdletters";
    txtLang[17].innerHTML = "Kleine letters";
    txtLang[18].innerHTML = "Geen";

    txtLang[19].innerHTML = "Niet gebruikt";
    txtLang[20].innerHTML = "Klein kapitaal";
    txtLang[21].innerHTML = "Normaal";

    txtLang[22].innerHTML = "Niet gebruikt";
    txtLang[23].innerHTML = "Superscript";
    txtLang[24].innerHTML = "Subscript";
    txtLang[25].innerHTML = "Relative";
    txtLang[26].innerHTML = "Basislijn";//Baseline

    txtLang[27].innerHTML = "Karakter Ruimte";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Standaard"
    optLang[1].text = "Cursief"
    optLang[2].text = "Vet"
    optLang[3].text = "Vet Cursief"
    
    optLang[0].value = "Standaard"
    optLang[1].value = "Cursief"
    optLang[2].value = "Vet"
    optLang[3].value = "Vet Cursief"
    
    sStyleWeight1 = "Standaard"
    sStyleWeight2 = "Cursief"
    sStyleWeight3 = "Vet"
    sStyleWeight4 = "Vet Cursief"
    
    optLang[4].text = "Boven"
    optLang[5].text = "Midden"
    optLang[6].text = "Onder"
    optLang[7].text = "Tekst-boven"
    optLang[8].text = "Tekst-onder"
    
    document.getElementById("btnPick1").value = "Kiezen";
    document.getElementById("btnPick2").value = "Kiezen";

    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnOk").value = " ok ";
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
    document.write("<title>Tekst Opmaak</title>")
    }