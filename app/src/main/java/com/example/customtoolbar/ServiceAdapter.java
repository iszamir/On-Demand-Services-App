package com.example.customtoolbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private Context context;
    private List<Service> dataList;

    public ServiceAdapter(Context context, List<Service> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_services,parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.title.setText(dataList.get(position).getTitle());
        holder.description.setText(dataList.get(position).getDescription());
        holder.phoneNumber.setText(dataList.get(position).getPhoneNumber());
        holder.price.setText(dataList.get(position).getPrice());
        holder.selectedService.setText(dataList.get(position).getSelectedService());
        holder.selectedState.setText(dataList.get(position).getSelectedState());
        holder.selectedDistrict.setText(dataList.get(position).getSelectedDistrict());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        TextView phoneNumber;
        TextView price;
        TextView selectedService;
        TextView selectedState;
        TextView selectedDistrict;
        CardView recCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recCard = itemView.findViewById(R.id.recycle_card);
            title = itemView.findViewById(R.id.textView_item_title);
            description = itemView.findViewById(R.id.textView_item_description);
            phoneNumber = itemView.findViewById(R.id.textView_item_phone_number);
            price = itemView.findViewById(R.id.textView_item_price);
            selectedService = itemView.findViewById(R.id.textView_item_service_type);
            selectedState = itemView.findViewById(R.id.textView_item_state);
            selectedDistrict = itemView.findViewById(R.id.textView_item_district);

        }
    }

}
