import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

# Fetch the service account key JSON file contents
cred = credentials.Certificate(
    'blacklist-49ebb-firebase-adminsdk-a235w-21b5c57205.json')
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://blacklist-49ebb-default-rtdb.asia-southeast1.firebasedatabase.app/'
})

user_data = '/userdata'
numbers_key = 'numbers'
BlackNumber = 'BlackNumber'
Default = 'DEFAULT'

while (True):
    default_blacklist = {}
    blacklist = {}
    whitelist = {}
    ref = db.reference(user_data)
    data = ref.get()
    default_blacklist = data[Default][numbers_key]
    for user in data:
        if user == Default:
            pass
        if numbers_key in data[user]:
            numbers = data[user][numbers_key]
            for number in numbers:
                if numbers[number] == BlackNumber:
                    if number not in blacklist:
                        blacklist[number] = 0
                    blacklist[number] += 1
                else:
                    if number not in whitelist:
                        whitelist[number] = 0
                    whitelist[number] += 1
    changing = False
    for number in blacklist:
        cntblack = blacklist[number] + 1
        cntwhite = 1
        if number in whitelist:
            cntwhite += whitelist[number]
        if cntblack > 2 and cntblack >= cntwhite * 2:
            if number not in default_blacklist:
                default_blacklist[number] = BlackNumber
                print("Adding: ", number)
                changing = True
    if changing:
        ref.child(Default).child(numbers_key).set(default_blacklist)
