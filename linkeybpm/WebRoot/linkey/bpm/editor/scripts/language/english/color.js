function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Web Pallete";
    txtLang[1].innerHTML = "Named Colors";
    txtLang[2].innerHTML = "216 Web Safe";
    txtLang[3].innerHTML = "New";
    txtLang[4].innerHTML = "Current";
    txtLang[5].innerHTML = "Custom colors";
    
    document.getElementById("btnAddToCustom").value = "Add to Custom Colors";
    document.getElementById("btnCancel").value = " cancel ";
    document.getElementById("btnRemove").value = " remove color ";
    document.getElementById("btnApply").value = " apply ";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Colors</title>")
    }
