# MedAssist Reminder System Documentation

## Overview
The MedAssist reminder system allows users to set medication reminders with specific dates and times. When the reminder time arrives, users receive a notification to take their medicine.

## Features Implemented

### 1. **Save Reminder from Medicine Details**
- Users can view medicine details in `DescriptionActivity`
- Click "Save Reminder" button to set a reminder
- Select date and time using DatePicker and TimePicker
- Reminder is saved to SharedPreferences and alarm is scheduled

### 2. **View All Reminders**
- Navigate to the Reminders tab in HomeActivity
- See all saved reminders in a list
- Each reminder shows:
  - Medicine name
  - Time (12-hour format with AM/PM)
  - Date (DD/MM/YYYY format)
  - Delete button

### 3. **Delete Reminders**
- Click the "Delete" button on any reminder
- Removes the reminder from storage
- Cancels the scheduled alarm
- Shows empty state when no reminders exist

### 4. **Notifications**
- When reminder time arrives, user receives a notification
- Notification shows: "It's time to take your [Medicine Name]"
- Clicking notification opens the app to the Reminders page
- High priority notification with sound and vibration

### 5. **Permissions Handling**
- Automatically requests notification permission (Android 13+)
- Requests exact alarm permission when needed (Android 12+)
- Shows helpful messages if permissions are denied

## Technical Architecture

### Files Modified/Created

#### Java Classes
1. **DescriptionActivity.java**
   - Shows medicine details
   - Handles "Save Reminder" button click
   - Shows date/time picker dialog
   - Schedules alarm using AlarmManager
   - Saves reminder data to SharedPreferences

2. **ReminderFragment.java**
   - Displays list of all reminders
   - Loads reminders from SharedPreferences
   - Handles reminder deletion
   - Shows empty state when no reminders

3. **ReminderAdapter.java**
   - RecyclerView adapter for reminder list
   - Formats date/time for display
   - Handles delete button clicks

4. **AlarmReceiver.java**
   - BroadcastReceiver that triggers when alarm fires
   - Creates and shows notification
   - Handles notification click to open app

5. **PermissionHelper.java** (NEW)
   - Centralized permission management
   - Checks notification permission (Android 13+)
   - Checks exact alarm permission (Android 12+)
   - Requests permissions when needed

6. **HomeActivity.java**
   - Handles navigation to reminder fragment
   - Requests notification permission on startup

#### Layout Files
1. **dialog_reminder.xml**
   - Dialog layout with DatePicker and TimePicker
   - Used when setting a new reminder

2. **fragment_reminder.xml**
   - Reminder list page layout
   - Contains RecyclerView and empty state TextView

3. **reminder_item.xml**
   - Individual reminder card layout
   - Shows medicine name, time, date, and delete button

#### AndroidManifest.xml
- Declares AlarmReceiver
- Requests permissions:
  - INTERNET
  - SCHEDULE_EXACT_ALARM
  - POST_NOTIFICATIONS

## Data Storage

### SharedPreferences Structure

#### Reminders Storage
- **Preference File**: "Reminders"
- **Key**: "AllReminders"
- **Type**: StringSet
- **Format**: Each string is formatted as: `"MedicineName|TimeInMillis|RequestCode"`

Example:
```
"Aspirin|1738324800000|1738234567890"
"Paracetamol|1738411200000|1738234567891"
```

### Alarm Scheduling

1. **AlarmManager** is used to schedule exact alarms
2. Each alarm has a unique **requestCode** (timestamp when created)
3. **PendingIntent** broadcasts to AlarmReceiver
4. Alarm type: `RTC_WAKEUP` (fires even if device is asleep)
5. Method: `setExactAndAllowWhileIdle()` for Android 12+

## User Flow

### Setting a Reminder
1. User searches for medicine
2. Clicks on medicine to view details
3. Clicks "Save Reminder" button
4. App checks permissions (requests if needed)
5. Dialog appears with date and time pickers
6. User selects date and time
7. Clicks "Save Reminder"
8. App validates time is in future
9. Alarm is scheduled
10. Reminder is saved to storage
11. User is navigated to Reminders page
12. Reminder appears in the list

### Receiving a Notification
1. Alarm fires at scheduled time
2. AlarmReceiver.onReceive() is called
3. Notification is created and shown
4. User sees notification in notification tray
5. User clicks notification
6. App opens to Reminders page
7. User can see all their reminders

### Deleting a Reminder
1. User navigates to Reminders page
2. Clicks "Delete" on a reminder
3. Reminder is removed from SharedPreferences
4. Scheduled alarm is cancelled
5. Reminder disappears from list
6. If no reminders left, empty state is shown

## Permissions Required

### Android 13+ (API 33+)
- **POST_NOTIFICATIONS**: Required to show notifications
- Requested automatically on app startup
- User can grant/deny in system settings

### Android 12+ (API 31+)
- **SCHEDULE_EXACT_ALARM**: Required to schedule exact alarms
- Requested when user tries to set a reminder
- Opens system settings page for user to grant

## Error Handling

1. **Past Time Selected**: Shows toast "Please select a future date and time!"
2. **No Notification Permission**: Shows toast and requests permission
3. **No Exact Alarm Permission**: Shows toast and opens settings
4. **Null Context**: Checks for null context before operations
5. **Invalid Reminder Data**: Validates data format before parsing

## Testing Checklist

- [ ] Set a reminder for 1 minute in the future
- [ ] Verify reminder appears in Reminders list
- [ ] Wait for notification to appear
- [ ] Click notification and verify app opens to Reminders
- [ ] Delete a reminder and verify it's removed
- [ ] Try to set a reminder in the past (should show error)
- [ ] Test on Android 13+ device (notification permission)
- [ ] Test on Android 12+ device (exact alarm permission)
- [ ] Set multiple reminders for different medicines
- [ ] Verify empty state shows when no reminders

## Future Enhancements

1. **Recurring Reminders**: Daily, weekly, or custom intervals
2. **Snooze Feature**: Delay reminder by 5/10/15 minutes
3. **Reminder Notes**: Add custom notes to reminders
4. **Dosage Tracking**: Mark when medicine is taken
5. **Reminder History**: View past reminders and adherence
6. **Multiple Reminders per Medicine**: Set different times for same medicine
7. **Sound Customization**: Choose notification sound
8. **Reminder Categories**: Organize by morning/afternoon/evening
9. **Export/Import**: Backup and restore reminders
10. **Widget**: Home screen widget showing upcoming reminders
