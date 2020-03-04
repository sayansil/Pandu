from google.cloud import firestore
import pandas as pd
import matplotlib.pyplot as plt
from base64 import b64encode
from io import BytesIO
from flask import Flask, render_template, request


app = Flask(__name__)


def get_names(db, pandel_id):
    names = []
    db = firestore.Client()
    pandel_ref = db.collection('pandels')
    for pandel in pandel_ref.stream():
        names.append((pandel.id, pandel.to_dict()['name']))
    return pd.DataFrame(names, columns=['pandel_id', 'pandel_name'])


def get_reviews():
    reviews = []
    db = firestore.Client()
    pandel_ref = db.collection('pandels')
    for pandel in pandel_ref.stream():
        print("Fetching data from: ", pandel.to_dict()['name'])
        review_ref = db.collection('pandels').document(pandel.id).collection('reviews')
        for review in review_ref.stream():
            review_info = review.to_dict()
            reviews.append((pandel.id, review_info['text'], review_info['uid']))
    return pd.DataFrame(reviews, columns=['pandel_id', 'review_text', 'user_id'])


def df_to_html(df, keep_index=True):
    pd.set_option('display.max_colwidth', None)
    return df.to_html(index=keep_index)


def get_static_plot():
    buf = BytesIO()
    plt.savefig(buf, format="png")
    data = b64encode(buf.getbuffer()).decode("ascii")
    data = "<img class='plot' src='data:image/png;base64," + data + "' />"
    return data


@app.route('/')
def main():
    plots = list()
    d_buttons = list()
    html_scripts = list()

    df = get_reviews()

    #########################
    #       All reviews     #
    #########################

    title = "All Reviews"
    plot_div = df_to_html(df)
    plot_div = plot_div.replace(
        '<table border="1" class="dataframe">',
        '<table id="table_div" class="table table-striped">')
    plot_div = plot_div.replace(
        '<tr style="text-align: right;">',
        '<tr style="text-align: left;">')
    plots.append({"title": title, "div": plot_div})
    d_buttons.append({"text": "Download Raw data", "target": "table_div"})

    return render_template('results.html', plots=plots, html_scripts=html_scripts, d_buttons=d_buttons)


@app.route('/ok')
def ok():
    return "ok"

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=True)