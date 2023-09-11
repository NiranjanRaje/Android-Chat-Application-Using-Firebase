package com.test.loginfirebase;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.loginfirebase.databinding.ItemContainerReceivedMessageBinding;
import com.test.loginfirebase.databinding.ItemContainerSentMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<ChatMessage> chatMessages;
    private final String senderId;
    private Bitmap receiveProfileImage;
    public static final int VIEW_TYPE_SENT=1;
    public static final int VIEW_TYPE_RECEIVE=2;

    public void setReceiveProfileImage(Bitmap bitmap)
    {
        receiveProfileImage=bitmap;

    }

    public ChatAdapter(List<ChatMessage> chatMessages, String senderId, Bitmap receiveProfileImage) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.receiveProfileImage = receiveProfileImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==VIEW_TYPE_SENT)
        {
            return new SendMessageViewHolder(ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }else
        {
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position)==VIEW_TYPE_SENT)
        {
            ((SendMessageViewHolder)holder).setData(chatMessages.get(position));
        }else
        {
            ((ReceivedMessageViewHolder)holder).setData(chatMessages.get(position),receiveProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId))
        {
            return VIEW_TYPE_SENT;
        }else
        {
            return VIEW_TYPE_RECEIVE;
        }
    }

    static class SendMessageViewHolder extends RecyclerView.ViewHolder
    {
        private final ItemContainerSentMessageBinding binding;
        SendMessageViewHolder(ItemContainerSentMessageBinding itemContainerUserBinding)
        {
            super(itemContainerUserBinding.getRoot());
            binding=itemContainerUserBinding;

        }
        void setData(ChatMessage chatMessage)
        {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder
    {
        private final ItemContainerReceivedMessageBinding binding;
        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding)
        {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding=itemContainerReceivedMessageBinding;

        }
        void setData(ChatMessage chatMessage,Bitmap receiverProfileImage)
        {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            if (receiverProfileImage!=null)
            {
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }
        }
    }
}
