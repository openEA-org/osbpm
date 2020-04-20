function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "L\u00E4hde";
    txtLang[1].innerHTML = "Tausta";
    txtLang[2].innerHTML = "Leveys";
    txtLang[3].innerHTML = "Korkeus";
    txtLang[4].innerHTML = "Laatu";
    txtLang[5].innerHTML = "Keskit\u00E4";
    txtLang[6].innerHTML = "Toisto";
    txtLang[7].innerHTML = "Kyll\u00E4";
    txtLang[8].innerHTML = "Ei";
    
    txtLang[9].innerHTML = "Class ID";
    txtLang[10].innerHTML = "CodeBase";
    txtLang[11].innerHTML = "PluginsPage";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Matala"
    optLang[1].text = "Korkea"
    optLang[2].text = "<Ei asetettu>"
    optLang[3].text = "Vasen"
    optLang[4].text = "Oikea"
    optLang[5].text = "Yl\u00F6s"
    optLang[6].text = "Alas"
    
    document.getElementById("btnPick").value = "Poimi";
    
    document.getElementById("btnCancel").value = "Peruuta";
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
    document.write("<title>"+"Liit\u00E4 Flash"+"</title>")
    }