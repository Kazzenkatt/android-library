#!/usr/bin/env python3
"""
AndroidX Migration Script
Automatically migrates Android Support Library imports to AndroidX
"""

import os
import re
from pathlib import Path

# Mapping of old support library imports to AndroidX equivalents
IMPORT_MAPPINGS = {
    # Support v4
    'android.support.v4.app.Fragment': 'androidx.fragment.app.Fragment',
    'android.support.v4.app.FragmentActivity': 'androidx.fragment.app.FragmentActivity',
    'android.support.v4.app.FragmentManager': 'androidx.fragment.app.FragmentManager',
    'android.support.v4.app.FragmentTransaction': 'androidx.fragment.app.FragmentTransaction',
    'android.support.v4.content.ContextCompat': 'androidx.core.content.ContextCompat',
    'android.support.v4.content.FileProvider': 'androidx.core.content.FileProvider',
    'android.support.v4.view.ViewCompat': 'androidx.core.view.ViewCompat',
    'android.support.v4.view.ViewPager': 'androidx.viewpager.widget.ViewPager',
    'android.support.v4.widget.DrawerLayout': 'androidx.drawerlayout.widget.DrawerLayout',
    'android.support.v4.graphics.ColorUtils': 'androidx.core.graphics.ColorUtils',
    
    # Support v7
    'android.support.v7.app.AppCompatActivity': 'androidx.appcompat.app.AppCompatActivity',
    'android.support.v7.app.AlertDialog': 'androidx.appcompat.app.AlertDialog',
    'android.support.v7.widget.RecyclerView': 'androidx.recyclerview.widget.RecyclerView',
    'android.support.v7.widget.LinearLayoutManager': 'androidx.recyclerview.widget.LinearLayoutManager',
    'android.support.v7.widget.GridLayoutManager': 'androidx.recyclerview.widget.GridLayoutManager',
    'android.support.v7.widget.Toolbar': 'androidx.appcompat.widget.Toolbar',
    'android.support.v7.widget.PopupMenu': 'androidx.appcompat.widget.PopupMenu',
    'android.support.v7.widget.AppCompatImageButton': 'androidx.appcompat.widget.AppCompatImageButton',
    'android.support.v7.view.ContextThemeWrapper': 'androidx.appcompat.view.ContextThemeWrapper',
    'android.support.v7.preference.PreferenceFragmentCompat': 'androidx.preference.PreferenceFragmentCompat',
    'android.support.v7.preference.Preference': 'androidx.preference.Preference',
    
    # Design/Material
    'android.support.design.widget.NavigationView': 'com.google.android.material.navigation.NavigationView',
    'android.support.design.widget.FloatingActionButton': 'com.google.android.material.floatingactionbutton.FloatingActionButton',
    'android.support.design.widget.Snackbar': 'com.google.android.material.snackbar.Snackbar',
    'android.support.design.widget.CoordinatorLayout': 'androidx.coordinatorlayout.widget.CoordinatorLayout',
    'android.support.design.widget.AppBarLayout': 'com.google.android.material.appbar.AppBarLayout',
    
    # Annotations
    'android.support.annotation.NonNull': 'androidx.annotation.NonNull',
    'android.support.annotation.Nullable': 'androidx.annotation.Nullable',
    'android.support.annotation.Keep': 'androidx.annotation.Keep',
    'android.support.annotation.RequiresApi': 'androidx.annotation.RequiresApi',
}

# Class name mappings (for XML and code references without imports)
CLASS_MAPPINGS = {
    'android.support.v4.widget.DrawerLayout': 'androidx.drawerlayout.widget.DrawerLayout',
    'android.support.design.widget.NavigationView': 'com.google.android.material.navigation.NavigationView',
    'android.support.design.widget.FloatingActionButton': 'com.google.android.material.floatingactionbutton.FloatingActionButton',
    'android.support.design.widget.CoordinatorLayout': 'androidx.coordinatorlayout.widget.CoordinatorLayout',
    'android.support.design.widget.AppBarLayout': 'com.google.android.material.appbar.AppBarLayout',
    'android.support.v7.widget.Toolbar': 'androidx.appcompat.widget.Toolbar',
    'android.support.constraint.ConstraintLayout': 'androidx.constraintlayout.widget.ConstraintLayout',
}

def migrate_file(file_path):
    """Migrate a single file from support library to AndroidX"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        changes = 0
        
        # Replace imports
        for old_import, new_import in IMPORT_MAPPINGS.items():
            old_pattern = f'import {old_import};'
            new_pattern = f'import {new_import};'
            if old_pattern in content:
                content = content.replace(old_pattern, new_pattern)
                changes += 1
        
        # Replace class references in XML and code
        for old_class, new_class in CLASS_MAPPINGS.items():
            if old_class in content:
                content = content.replace(old_class, new_class)
                changes += 1
        
        # Write back if changed
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            return changes
        
        return 0
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return 0

def migrate_project(project_root):
    """Migrate entire project"""
    project_path = Path(project_root)
    
    # Find all Java and XML files
    java_files = list(project_path.glob('app/src/**/*.java'))
    xml_files = list(project_path.glob('app/src/**/*.xml'))
    
    total_changes = 0
    files_modified = 0
    
    print(f"Found {len(java_files)} Java files and {len(xml_files)} XML files")
    print("Migrating...")
    
    for file_path in java_files + xml_files:
        changes = migrate_file(file_path)
        if changes > 0:
            files_modified += 1
            total_changes += changes
            print(f"  ✓ {file_path.relative_to(project_path)} ({changes} changes)")
    
    print(f"\n✅ Migration complete!")
    print(f"   Files modified: {files_modified}")
    print(f"   Total changes: {total_changes}")
    
    return files_modified > 0

if __name__ == '__main__':
    project_root = '/mnt/c/dev/AndroidStudioProjects/Project01'
    
    print("=" * 60)
    print("AndroidX Migration Script")
    print("=" * 60)
    print(f"Project: {project_root}\n")
    
    success = migrate_project(project_root)
    
    if success:
        print("\n📝 Next steps:")
        print("   1. Review the changes with: git diff")
        print("   2. Update dependencies in app/build.gradle")
        print("   3. Enable AndroidX in gradle.properties")
        print("   4. Build the project")
    else:
        print("\n⚠️  No changes made - files may already be migrated")
