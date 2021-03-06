package treehou.se.habit.ui.widgets.factories;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class SelectionWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(final WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {

        return new SelectWidgetHolder(widget, parent, widgetFactory);
    }

    public static class SelectWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "SelectWidgetHolder";

        private View itemView;
        private Spinner sprSelect;
        private WidgetFactory factory;

        private int lastPosition = -1;

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;

        public SelectWidgetHolder(Widget widget, Widget parent, WidgetFactory factory) {
            this.factory = factory;

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                    .setWidget(widget)
                    .setParent(parent)
                    .build();

            itemView = factory.getInflater().inflate(R.layout.item_widget_selection, null);
            sprSelect = (Spinner) itemView.findViewById(R.id.spr_selector);
            baseHolder.getSubView().addView(itemView);

            update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }

        @Override
        public void update(final Widget widget) {
            Log.d(TAG, "update " + widget);
            if (widget == null) {
                return;
            }

            sprSelect.setOnItemSelectedListener(null);

            final List<Widget.Mapping> mappings = widget.getMapping();
            final ArrayAdapter<Widget.Mapping> mappingAdapter = new ArrayAdapter<>(factory.getContext(), R.layout.item_text, mappings);
            sprSelect.setAdapter(mappingAdapter);
            for(int i=0; i<mappings.size(); i++){
                if (mappings.get(i).getCommand().equals(widget.getItem().getState())){
                    sprSelect.setSelection(i);
                    lastPosition = i;
                    break;
                }
            }

            //TODO request value
            // Prevents rouge initial fire
            sprSelect.post(new Runnable() {
                @Override
                public void run() {
                    sprSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position != lastPosition) {
                                Widget.Mapping mapping = mappings.get(position);
                                Communicator communicator = Communicator.instance(factory.getContext());
                                communicator.command(factory.getServer(), widget.getItem(), mapping.getCommand());
                                lastPosition = position;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                    mappingAdapter.notifyDataSetChanged();
                }
            });

            baseHolder.update(widget);
        }
    }
}
