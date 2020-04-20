function loadTxt()
	{
	document.getElementById("btnCheckAgain").value = " Check Again ";
    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnOk").value = " ok ";
	}
function getTxt(s)
	{
	switch(s)
		{
		case "Required":
			return "ieSpell (from www.iespell.com) is required.";
		default:return "";
		}
	}
function writeTitle()
	{
	document.write("<title>Check Spelling</title>")
	}