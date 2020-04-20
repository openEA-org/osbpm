function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "L\u00E4hde";
    txtLang[1].innerHTML = "Leveys";
    txtLang[2].innerHTML = "Korkeus";   
    txtLang[3].innerHTML = "Automaattinen k\u00E4ynnistys";  
    txtLang[4].innerHTML = "N\u00E4yt\u00E4 painikkeet";
    txtLang[5].innerHTML = "N\u00E4yt\u00E4 tilaikkuna";    
    txtLang[6].innerHTML = "N\u00E4yt\u00E4 n\u00E4ytt\u00F6";
    txtLang[7].innerHTML = "Automaattinen kelaus";  

    document.getElementById("btnCancel").value = "Peruuta";
    document.getElementById("btnInsert").value = "Liit\u00E4";
    document.getElementById("btnApply").value = "K\u00E4yt\u00E4";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>"+"Liit\u00E4 media"+"</title>")
    }
