package shree.firebaseandroid.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import shree.firebaseandroid.R;
import shree.firebaseandroid.model.ChatModel;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import shree.firebaseandroid.utils.Util;

public class ChatFirebaseAdapter extends FirebaseRecyclerAdapter<ChatModel,ChatFirebaseAdapter.MyChatViewHolder> {

    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;
    private static final int LEFT_MSG_PDF = 4;
    private static final int RIGHT_MSG_PDF = 5;

    byte[] imageBytes;

    private ClickListenerChatFirebase mClickListenerChatFirebase;
    private Context mContext;
    private String nameUser,pdfname;
    TextView tvTimestamp,tvLocation,sendername,tvpdfname;
    EmojiconTextView txtMessage;
    ImageView ivUser,ivChatPhoto,ivtick,ivpdfimage;

    public ChatFirebaseAdapter(Context context,DatabaseReference ref, String nameUser, ClickListenerChatFirebase mClickListenerChatFirebase) {
        super(ChatModel.class, R.layout.item_message_left, ChatFirebaseAdapter.MyChatViewHolder.class, ref);
        this.nameUser = nameUser;
        this.mClickListenerChatFirebase = mClickListenerChatFirebase;
       // this.mContext=context;
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
         mContext = parent.getContext();
        if (viewType == RIGHT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == LEFT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new MyChatViewHolder(view);
        } else if(viewType==RIGHT_MSG_PDF)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_right, parent, false);
            return new MyChatViewHolder(view);
        } else if(viewType==LEFT_MSG_PDF){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_left, parent, false);
            return new MyChatViewHolder(view);

        } else if (viewType == RIGHT_MSG_IMG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img, parent, false);
            return new MyChatViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img, parent, false);
            return new MyChatViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel model = getItem(position);
        if (model.getMapModel() != null) {
            if (model.getUserModel().getName().equals(nameUser)) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else if (model.getFile() != null) {

            if(model.getFile().getType().equals("img")&& model.getUserModel().getName().equals(nameUser))
            {
                return RIGHT_MSG_IMG;
            }
            else if(model.getFile().getType().equals("pdffile")&& model.getUserModel().getName().equals(nameUser))
            {
                return RIGHT_MSG_PDF;
            }
            else
            {
                if(model.getFile().getType().equals("img"))
                {
                      return LEFT_MSG_IMG;
                }else
                {
                    return LEFT_MSG_PDF;
                }

            }
        } else if (model.getUserModel().getName().equals(nameUser)) {
            return RIGHT_MSG;
        } else {
            return LEFT_MSG;
        }
    }

    @Override
    protected void populateViewHolder(MyChatViewHolder viewHolder, ChatModel model, int position) {
        viewHolder.setIvUser(model.getUserModel().getPhoto_profile());
        viewHolder.setTxtSenderName(model.getUserModel().getName());
        viewHolder.setTxtMessage(model.getMessage());
        viewHolder.setTvTimestamp(model.getTimeStamp());
        viewHolder.tvIsLocation(View.GONE);
        if (model.getFile() != null){
            viewHolder.tvIsLocation(View.GONE);
            if(model.getFile().getType().equals("img")) {
                viewHolder.setIvChatPhoto(model.getFile().getUrl_file());
            }else if(model.getFile().getType().equals("pdffile"))
            {
                pdfname=model.getFile().getName_file()+".pdf";
                viewHolder.setPdfName(model.getFile().getName_file()+".pdf");
                viewHolder.setPDFDownload(model.getFile().getUrl_file());
            }
        }else if(model.getMapModel() != null){
            viewHolder.setIvChatPhoto(shree.firebaseandroid.utils.Util.local(model.getMapModel().getLatitude(),model.getMapModel().getLongitude()));
            //viewHolder.tvIsLocation(View.VISIBLE);
        }


    }

    public class MyChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public MyChatViewHolder(View itemView) {
            super(itemView);
            tvTimestamp = (TextView)itemView.findViewById(R.id.timestamp);
            sendername=(TextView)itemView.findViewById(R.id.msgsendername);
            txtMessage = (EmojiconTextView)itemView.findViewById(R.id.txtMessage);
            tvLocation = (TextView)itemView.findViewById(R.id.tvLocation);
            ivChatPhoto = (ImageView)itemView.findViewById(R.id.img_chat);
            ivUser = (ImageView)itemView.findViewById(R.id.ivUserChat);
            ivtick=(ImageView)itemView.findViewById(R.id.tickimage);
            tvpdfname=(TextView)itemView.findViewById(R.id.pdffilename);
            ivpdfimage=(ImageView)itemView.findViewById(R.id.pdfimage);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ChatModel model = getItem(position);
            if (model.getMapModel() != null){
                mClickListenerChatFirebase.clickImageMapChat(view,position,model.getMapModel().getLatitude(),model.getMapModel().getLongitude());
            }else{
                if(model.getFile()!=null)
                {
                    if(model.getFile().getType().equals("pdffile"))
                    {
                        new DownloadFileFromURL().execute(model.getFile().getUrl_file().toString());
                    }
                    else
                    {
                        mClickListenerChatFirebase.clickImageChat(view,position,model.getUserModel().getName(),model.getUserModel().getPhoto_profile(),model.getFile().getUrl_file());
                    }
                }
                }
        }


        public void setTxtMessage(String message){
            if (txtMessage == null)
                return;
                txtMessage.setText(message);

        }

        public void setIvUser(String urlPhotoUser){
            if (ivUser == null)return;
           // Glide.with(ivUser.getContext()).load(urlPhotoUser).centerCrop().transform(new CircleTransform(ivUser.getContext())).override(40,40).into(ivUser);
            if(urlPhotoUser.length()>5) {
                //decode base64 string to image
                imageBytes = Base64.decode(urlPhotoUser, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                ivUser.setImageBitmap(decodedImage);
            }
        }

        public void setTvTimestamp(String timestamp){
            if (tvTimestamp == null)return;
                tvTimestamp.setText(converteTimestamp(timestamp));
        }

        public void setIvChatPhoto(final String url){

            if (ivChatPhoto == null)return;
            Glide.with(ivChatPhoto.getContext()).load(url)
                    .override(100, 100)
                    .fitCenter()
                    .into(ivChatPhoto);
            // Show progress bar

            /*Picasso.with(ivChatPhoto.getContext()).load(url).fit().centerCrop().networkPolicy(NetworkPolicy.OFFLINE).into(ivChatPhoto, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ivChatPhoto.getContext()).load(url).fit().centerCrop().into(ivChatPhoto);
                }
                });*/
            ivChatPhoto.setOnClickListener(this);
        }

        public void setPdfName(final  String pdffilename)
        {
            if (tvpdfname == null)return;
            tvpdfname.setText(pdffilename);
        }

        public void setPDFDownload(final String url)
        {
            /*URI url=new URI(this);
            url.getPath(url);*/
           //Bitmap bmp= generateImageFromPdf(url);
            if (ivpdfimage == null)return;
            ivpdfimage.setImageResource(R.drawable.pdf);
            ivpdfimage.setOnClickListener(this);
        }

        public void tvIsLocation(int visible){
            if (tvLocation == null)return;
            tvLocation.setVisibility(visible);
            tvLocation.setOnClickListener(this);
        }

        public void setTxtSenderName(String name) {
            if (sendername==null)return;
            sendername.setText(name);
        }

    }

    private CharSequence converteTimestamp(String mileSegundos){

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateString = formatter.format(new Date(Long.parseLong(mileSegundos)));
        return dateString;
        //return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }


    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        // Progress Dialog
        private ProgressDialog pDialog;

        // Progress dialog type (0 - for Horizontal progress bar)
        public static final int progress_bar_type = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream("/sdcard/Documents/"+pdfname);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
           // pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
             //dismissDialog(progress_bar_type);

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            // String imagePath = Environment.getExternalStorageDirectory().toString() + "/sdcard/Documents/"+pdfname;
            // setting downloaded into image view
           // ivpdfimage.setImageDrawable(Drawable.createFromPath(imagePath));

        }

    }


    Bitmap generateImageFromPdf(String pdfUri) {

        Uri url=Uri.parse(pdfUri);
        Bitmap bmp=null;

        int pageNumber = 1;
        PdfiumCore pdfiumCore = new PdfiumCore(mContext);
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            ParcelFileDescriptor fd = mContext.getContentResolver().openFileDescriptor(url, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            bmp= Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            saveImage(bmp);

           pdfiumCore.closeDocument(pdfDocument); // important!
        } catch(Exception e) {
            //todo with exception
            e.getMessage().toString();
        }

        return bmp;
    }

    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";
    private void saveImage(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            File folder = new File(FOLDER);
            if(!folder.exists())
                folder.mkdirs();
            File file = new File(folder, "PDF.png");
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            //todo with exception
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //todo with exception
            }
        }
    }

}
