package com.example.kareem.IIFYM_Tracker.ViewComponents;

/**
 * Created by Kareem on 9/13/2016.
 */

/*public class SavedMealAdapter extends ArrayAdapter<Food>{
    private SQLiteConnector My_DB;

    private ArrayList<Food> arrayList_Meals_original;
    private ArrayList<Food> arrayList_Meals_filtered;
    private Filter filter;

    float serving_number;
    Weight weight;
    int multiplier;

    public SavedMealAdapter(Context context, ArrayList<Food> foods) {
        super(context, 0, foods);
        arrayList_Meals_original = new ArrayList<Food>(foods);
        arrayList_Meals_filtered = new ArrayList<Food>(foods);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        My_DB = new SQLiteConnector(getContext());
        // Get the data item for this position
        Food M = getItem(position);
        int meal_id = M.getMeal_id();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_meal, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.Text_MealName);
        TextView carbs = (TextView) convertView.findViewById(R.id.Text_Carbs);
        TextView protein = (TextView) convertView.findViewById(R.id.Text_Protein);
        TextView fat = (TextView) convertView.findViewById(R.id.Text_Fat);
        TextView portion = (TextView) convertView.findViewById(R.id.Text_PortionDetails);

        // Populate the data into the template view using the data object
        name.setText(M.getMeal_name());
        carbs.setText(String.valueOf(M.getCarbs()));
        protein.setText(String.valueOf(M.getProtein()));
        fat.setText(String.valueOf(M.getFat()));

        if (M.getPortion() == Portion_Type.Serving) {
            serving_number = My_DB.getServing(meal_id);
            if (serving_number == 1.0f) {
                portion.setText(serving_number + " Serving");
            } else {
                portion.setText(serving_number + " Servings");
            }
        } else if (M.getPortion() == Portion_Type.Weight) {
            weight = My_DB.getWeight(meal_id);
            Log.d("Weight Retrieved: ", "ID: " + meal_id + " Weight_quantity: " + weight.getWeight_quantity() + " Weight_Unit: " + weight.getWeight_unit());
            portion.setText(weight.getWeight_quantity() + " " + weight.getWeight_unit().Abbreviate());
        }

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new myFilter();

        return filter;
    }

    private class myFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();

            if(prefix == null || prefix.length() == 0) {
                ArrayList<Food> list = new ArrayList<Food>(arrayList_Meals_original);
                results.values = list;
                results.count = list.size();
            }
            else {
                final ArrayList<Food> list = new ArrayList<Food>(arrayList_Meals_original);
                final ArrayList<Food> nlist = new ArrayList<Food>();
                int count = list.size();

                for (int i=0; i<count; i++){
                    final Food M = list.get(i);
                    final String value = M.getMeal_name().toLowerCase();

                    if(value.startsWith(prefix)){
                        nlist.add(M);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            arrayList_Meals_filtered = (ArrayList<Food>)results.values;

            clear();
            int count = arrayList_Meals_filtered.size();
            for (int i=0; i<count; i++)
            {
                Food M = (Food) arrayList_Meals_filtered.get(i);
                add(M);
            }
        }
    }
}
*/
