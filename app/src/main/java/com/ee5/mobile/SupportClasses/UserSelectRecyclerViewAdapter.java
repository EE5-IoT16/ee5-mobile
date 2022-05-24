
package com.ee5.mobile.SupportClasses;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import java.util.ArrayList;

        import com.ee5.mobile.R;

public class UserSelectRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context myContext;
    ArrayList<User> users;
    private OnItemClickListener myListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.userselect_item, parent, false);
        return new ViewHolderSelectUser(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderSelectUser viewHolderSelectUser = (ViewHolderSelectUser) holder;
        User currentItem = users.get(position);
        viewHolderSelectUser.userName.setText("test");
                        //currentItem.getUserFirstName() + " " + currentItem.getUserSurname()
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        myListener = listener;
    }

    public UserSelectRecyclerViewAdapter(Context context, ArrayList<User> users) {
        myContext = context;
        this.users = users;
    }

    class ViewHolderSelectUser extends RecyclerView.ViewHolder {
        public TextView userName;

        public ViewHolderSelectUser(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.rv_userselect_name);

            itemView.setOnClickListener(v -> {
                if (myListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        myListener.onItemClick(position);
                    }
                }
            });
        }
    }
}
