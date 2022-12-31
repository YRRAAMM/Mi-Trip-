package com.example.mi_trip.ui.home.addButtonActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mi_trip.R;
import com.example.mi_trip.databinding.AddNoteBinding;
import com.example.mi_trip.models.NotesModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddNoteFragment extends Fragment {

    private AddNoteBinding binding;
    List<TextInputLayout> mNotesTextInputLayout = new ArrayList<>();
    int increasedID = 0;

    List<String> notesList = new ArrayList<String>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = AddNoteBinding.inflate(inflater, container, false);

        // add first Note to mNotesTextInputLayout !
        mNotesTextInputLayout.add(binding.noteTextField);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addNoteBtn.setOnClickListener(v -> generateNoteLayout());
            // model has list of notes
            // here I have list of textfield
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (TextInputLayout txtLayout : mNotesTextInputLayout) {
                    Log.i("Notes List", Objects.requireNonNull(txtLayout.getEditText()).toString());

                    notesList.add(txtLayout.getEditText().getText().toString());
                    // send in an intent to the main add fragment.

                }
                NotesModel notesModel = new NotesModel(notesList);

                Intent notes = new Intent();
                notes.putExtra("notes", notesModel);

                NavHostFragment.findNavController(AddNoteFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void generateNoteLayout() {
        LinearLayout currentParent = binding.notesParentLinearLayout;

        View linearLayout = getLayoutInflater().inflate(R.layout.add_notes_sayout_sample, null);

        TextInputLayout noteTextInput = linearLayout.findViewById(R.id.note_text_field_input);
        mNotesTextInputLayout.add(noteTextInput);

        ImageButton subImgBtn = linearLayout.findViewById(R.id.sub_note_img_btn);
        subImgBtn.setOnClickListener(v -> {
            currentParent.removeView(linearLayout);
            mNotesTextInputLayout.remove(noteTextInput);
        });

        currentParent.addView(linearLayout);
        increasedID++;

    }

}