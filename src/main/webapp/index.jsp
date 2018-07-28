<html>
<body>
<h2>Hello World!</h2>

upload 文件
<form name="form1" action="/eshop/manager/product/upload.do" method="post" enctype="multipart/form-data">
    <input name="uploadFile" type="file" />
    <input name="submit" type="submit" value="upload文件测试 " />
</form>

rich edit
<form name="form2" action="/eshop/manager/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input name="uploadFile" type="file"  />
    <input name="submit" type="submit" value="富文本文件上传测试 " />
</form>

</body>
</html>
