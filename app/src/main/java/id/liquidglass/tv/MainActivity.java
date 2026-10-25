package id.liquidglass.tv;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, dock, allApps;
    TextView clock, date, hint;
    final ArrayList<AppEntry> apps = new ArrayList<>();
    int selected = 0;
    boolean showingAll = false;

    static class AppEntry {
        String label, pkg;
        android.graphics.drawable.Drawable icon;
        AppEntry(String l, String p, android.graphics.drawable.Drawable i) { label=l; pkg=p; icon=i; }
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        loadApps();
        buildHome();
        new android.os.Handler().post(new Runnable() {
            @Override public void run() {
                if (clock != null) clock.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                if (date != null) date.setText(new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(new Date()));
                new android.os.Handler().postDelayed(this, 30000);
            }
        });
    }

    void loadApps() {
        apps.clear();
        PackageManager pm = getPackageManager();
        Intent leanback = new Intent(Intent.ACTION_MAIN); leanback.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(leanback, 0);
        Intent launcher = new Intent(Intent.ACTION_MAIN); launcher.addCategory(Intent.CATEGORY_LAUNCHER);
        list.addAll(pm.queryIntentActivities(launcher, 0));
        HashSet<String> seen = new HashSet<>();
        for (ResolveInfo ri : list) {
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(getPackageName()) || seen.contains(pkg)) continue;
            seen.add(pkg);
            try { apps.add(new AppEntry(ri.loadLabel(pm).toString(), pkg, ri.loadIcon(pm))); } catch(Exception ignored) {}
        }
        Collections.sort(apps, (a,b) -> a.label.compareToIgnoreCase(b.label));
    }

    GradientDrawable shape(int color, int stroke, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius);
        if (stroke != 0) d.setStroke(2, stroke);
        return d;
    }

    TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return t;
    }

    void buildHome() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(58, 34, 58, 30);
        root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
            new int[]{Color.rgb(11,18,39), Color.rgb(29,42,77), Color.rgb(10,15,31)}));
        setContentView(root);

        LinearLayout top = new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL);
        TextView brand = text("LIQUID GLASS", 15, 0xFFBFD1F8, true);
        brand.setLetterSpacing(.18f);
        top.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        TextView status = text("SHARP AQUOS  •  ANDROID TV", 12, 0xFFA8B5D3, false);
        top.addView(status);
        root.addView(top);

        LinearLayout hero = new LinearLayout(this); hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(0, 30, 0, 18);
        clock = text("--:--", 58, Color.WHITE, false);
        date = text("", 18, 0xFFD0D9EF, false);
        hero.addView(clock); hero.addView(date);
        TextView title = text("Your screen. Your space.", 24, Color.WHITE, true);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(-2,-2); tp.topMargin=18;
        hero.addView(title,tp);
        TextView subtitle = text("A calm, lightweight home for everything you watch.", 14, 0xFFB8C5E1, false);
        hero.addView(subtitle);
        root.addView(hero);

        TextView label = text("FAVORIT", 13, 0xFFBFD1F8, true);
        label.setLetterSpacing(.12f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2,-2); lp.topMargin=6; lp.bottomMargin=10;
        root.addView(label,lp);

        HorizontalScrollView hsv = new HorizontalScrollView(this); hsv.setHorizontalScrollBarEnabled(false);
        dock = new LinearLayout(this); dock.setOrientation(LinearLayout.HORIZONTAL);
        hsv.addView(dock); root.addView(hsv, new LinearLayout.LayoutParams(-1, 122));
        rebuildDock();

        hint = text("← → pilih     OK buka     ↓ semua aplikasi     BACK kembali", 12, 0xFF8F9FBE, false);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1,-2); hp.topMargin=18;
        root.addView(hint,hp);
        getWindow().getDecorView().setFocusableInTouchMode(true);
        getWindow().getDecorView().requestFocus();
    }

    void rebuildDock() {
        dock.removeAllViews();
        int count = Math.min(6, apps.size());
        if (count == 0) {
            TextView empty = text("Belum ada aplikasi yang terdeteksi.", 16, Color.WHITE, false);
            dock.addView(empty); return;
        }
        for (int i=0;i<count;i++) {
            final int idx=i; AppEntry app=apps.get(i);
            LinearLayout tile = new LinearLayout(this); tile.setOrientation(LinearLayout.VERTICAL);
            tile.setGravity(Gravity.CENTER); tile.setPadding(12,12,12,10);
            tile.setBackground(shape(0x24FFFFFF, 0x55FFFFFF, 22));
            ImageView icon = new ImageView(this); icon.setImageDrawable(app.icon);
            tile.addView(icon, new LinearLayout.LayoutParams(48,48));
            TextView name = text(app.label, 12, Color.WHITE, true);
            name.setGravity(Gravity.CENTER); name.setMaxLines(1); name.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(105,-2); np.topMargin=5;
            tile.addView(name,np);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(130,112); p.rightMargin=12;
            dock.addView(tile,p);
            tile.setFocusable(true);
            tile.setOnFocusChangeListener((v, focus) -> {
                v.setBackground(shape(focus ? 0x55FFFFFF : 0x24FFFFFF,
                    focus ? 0xFFFFFFFF : 0x55FFFFFF, focus ? 26 : 22));
                v.setScaleX(focus ? 1.06f : 1f); v.setScaleY(focus ? 1.06f : 1f);
            });
            tile.setOnClickListener(v -> launch(app));
            tile.setOnKeyListener((v,key,event) -> {
                if(event.getAction()==KeyEvent.ACTION_DOWN && key==KeyEvent.KEYCODE_DPAD_DOWN) { showAll(); return true; }
                return false;
            });
        }
        if (dock.getChildCount()>0) dock.getChildAt(0).requestFocus();
    }

    void showAll() {
        showingAll=true;
        root.removeAllViews();
        TextView title = text("Semua aplikasi", 30, Color.WHITE, true);
        root.addView(title);
        TextView sub = text("Pilih aplikasi dengan remote TV", 14, 0xFFB8C5E1, false);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-2,-2); sp.bottomMargin=20; root.addView(sub,sp);
        ScrollView scroll = new ScrollView(this);
        allApps = new LinearLayout(this); allApps.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(allApps); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        for (AppEntry app: apps) {
            LinearLayout row = new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(14,10,14,10); row.setBackground(shape(0x24FFFFFF,0x33FFFFFF,16));
            ImageView icon = new ImageView(this); icon.setImageDrawable(app.icon);
            row.addView(icon,new LinearLayout.LayoutParams(38,38));
            TextView name = text(app.label,17,Color.WHITE,true);
            LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(0,-2,1); np.leftMargin=16; row.addView(name,np);
            allApps.addView(row,new LinearLayout.LayoutParams(-1,60));
            LinearLayout.LayoutParams rp = (LinearLayout.LayoutParams)row.getLayoutParams(); rp.bottomMargin=7; row.setLayoutParams(rp);
            row.setFocusable(true); row.setOnClickListener(v -> launch(app));
            row.setOnFocusChangeListener((v,f)->v.setBackground(shape(f?0x66FFFFFF:0x24FFFFFF,f?Color.WHITE:0x33FFFFFF,16)));
        }
        TextView back = text("BACK untuk kembali ke beranda",12,0xFF8F9FBE,false);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1,-2); bp.topMargin=10; root.addView(back,bp);
        if(allApps.getChildCount()>0) allApps.getChildAt(0).requestFocus();
    }

    void launch(AppEntry app) {
        try { Intent i=getPackageManager().getLaunchIntentForPackage(app.pkg); if(i!=null) startActivity(i); }
        catch(Exception ignored) {}
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && showingAll) { showingAll=false; buildHome(); return true; }
        if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN && !showingAll) { showAll(); return true; }
        return super.onKeyDown(keyCode,event);
    }
}
