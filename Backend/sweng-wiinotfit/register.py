import webapp2
from google.appengine.ext import ndb
from User.py import User

def json_response( status ):
    if status == 0:
        res = """{
    "register": {
        "status": "ok"
    }
}"""
    elif status == 1:
        res = """{
    "register": {
        "status": "failure",
        "reason": "email"
    }
}"""
    elif status == 2:
        res = """{
    "register": {
        "status": "failure",
        "reason": "username"
    }
}"""
    elif status == -1:
        res = """{
    "register": {
        "status": "invalid"
    }
}"""
    return res
    
class Register(webapp2.RequestHandler):            
    
    def get(self):
        username = self.request.get("user")
        password = self.request.get("password")
        email = self.request.get("email")
       
        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'
         
        if not username or not password or not email:
            # some fields were missing in the request
            self.response.write(json_response(-1))
        else:
            query_user = User.query(User.username == username).fetch()
            query_email = User.query(User.email == email).fetch()
            if query_user:
                # a user with this username exists already
                self.response.write(json_response(2))
            elif query_email:
                # a user with this email exists already
                self.response.write(json_response(1))
            else: 
                # create a new user with the data recieved
                user = User(username = username, pswd = password, email = email)
                db_result = user.put()
                self.response.write(json_response(0))

app = webapp2.WSGIApplication([
    ('/register', Register),
], debug=True)