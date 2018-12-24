````aidl
 //下载
$('#down').on('click',function () {

    $.loading()

    var param = {}

    billCommonQuery.queryDownLoadRest(initSearchParam(param),function (data) {

        $.loading()

        console.log(initSearchParam(param));

        var fileHref = host + '/p-trade-oc-admin/orderApi/admin/exportSalesOrder/export?'+$.paramJoin(initSearchParam(param))

        console.log(fileHref)

        $('iframe').remove()
        var iframe = document.createElement('iframe');
        iframe.id = "iframe1",iframe.name = "iframe1";
        iframe.frameBorder = "0", iframe.border = "0", iframe.allowtransparency = "true"
        iframe.width = 0, iframe.height = 0,iframe.style.display = 'none'
        iframe.src = fileHref;
        $('body').append(iframe);
        setTimeout(function () {
            $.loadingHide();
        },500)

    })
})
````