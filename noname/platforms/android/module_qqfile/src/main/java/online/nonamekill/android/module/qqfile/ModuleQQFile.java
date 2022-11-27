package online.nonamekill.android.module.qqfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import online.nonamekill.common.module.BaseModule;

public class ModuleQQFile extends BaseModule {

    @Override
    public View getView(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.module_qqfile_layout, null);

        Button viewById = inflate.findViewById(R.id.module_qqfile_button);
        viewById.setOnClickListener((v) -> {
            try {
                Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata/com.tencent.mobileqq");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                getActivity().startActivityForResult(intent, 6666);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return inflate;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public String getName() {
        return "QQ文件";
    }
}
