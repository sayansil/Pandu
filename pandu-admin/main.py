from google.cloud import firestore
import pandas as pd
import matplotlib.pyplot as plt
from base64 import b64encode
from io import BytesIO
from flask import Flask, render_template, request
from wtforms import StringField, BooleanField, SelectField, validators
from flask_wtf import FlaskForm
import json
import dill
from pymystem3 import Mystem
from stopwords import stopwords
import numpy as np
import re
from umap import UMAP
import bokeh.plotting as bp
from bokeh.models import HoverTool, TapTool, OpenURL
from bokeh.plotting import figure, show, output_notebook, reset_output
from bokeh.palettes import d3
import bokeh.models as bmo
import bokeh.io
from bokeh.embed import components


def create_app():
    app = Flask(__name__)
    return app

app = create_app()


with open('config.json') as config_file:
    configs = json.load(config_file)
    app.config['SECRET_KEY'] = configs['SECRET_KEY']


def get_names():
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


def generate_vectors(docs):
    docs = remove_strange_char(docs)
    docs = tokenize(docs)
    docs = lemmatize(docs)
    docs = remove_stopwords(docs)
    vectorizer = dill.load(open("pickles/imdb_tfidf_py38.sav", 'rb'))
    vectors = vectorizer.transform(docs).toarray()
    features = np.array(vectorizer.get_feature_names())
    return vectors, features


def remove_strange_char(docs):
    return [re.sub(r'[^a-zA-Z #_+]', r'', doc) for doc in docs]


def tokenize(docs):
    return [doc.lower().split() for doc in docs]


def lemmatize(docs):
    lemm = Mystem()
    return [[''.join(lemm.lemmatize(word)).replace('\n', '') for word in doc] for doc in docs]


def remove_stopwords(docs: list) -> list:
    return [[word for word in doc if len(word) > 1 and word not in stopwords] for doc in docs]


def results_on(pandel_id):
    plots = list()
    d_buttons = list()
    html_scripts = list()

    df = get_reviews()
    name_map = get_names()
    name_map = dict(zip(name_map['pandel_id'], name_map['pandel_name']))
    df['pandel_name'] = [name_map[i] for i in df['pandel_id']]
    if pandel_id != '--all--':
        df = df.loc[df['pandel_id'] == pandel_id]

    #############################
    #       Cluster UMAP        #
    #############################

    title = "2D map of reviews"
    vectors, features = generate_vectors(list(df['review_text']))
    fit = UMAP(
        n_neighbors=5,
        min_dist=0.2,
        n_components=2,
        metric='cosine')
    u = fit.fit_transform(vectors)
    df["plot_x"] = u[:, 0]
    df["plot_y"] = u[:, 1]
    plot_df = bp.figure(
        plot_width=700,
        plot_height=600,
        tools="pan,wheel_zoom,box_zoom,reset,hover",
        x_axis_type=None,
        y_axis_type=None,
        min_border=1)
    pandel_ids = list(set(df['pandel_id']))
    color = d3['Category10']
    palette = color[len(pandel_ids)] if len(pandel_ids) > 2 else color[3][0:len(pandel_ids)]
    groups = df.groupby('pandel_id')
    for group in groups:
        color = palette[pandel_ids.index(group[0])-1]
        plot_df.scatter(
            x="plot_x",
            y="plot_y",
            fill_color=color,
            size=10,
            legend_group='pandel_name',
            source=group[1])
    hover = plot_df.select(dict(type=HoverTool))
    tooltip_dict = dict()
    tooltip_dict['pandel_id'] = "@" + 'pandel_id'
    tooltip_dict['user_id'] = "@" + 'user_id'
    tooltip_dict['review_text'] = "@" + 'review_text'
    hover.tooltips = tooltip_dict
    script, plot_div = components(plot_df)
    html_scripts.append(script)
    plots.append({"title": title, "div": plot_div})

    #########################
    #       All reviews     #
    #########################

    title = "All Reviews"
    view_df = df[['pandel_name', 'review_text', 'user_id']]
    plot_div = df_to_html(view_df)
    plot_div = plot_div.replace(
        '<table border="1" class="dataframe">',
        '<table id="table_div" class="table table-striped">')
    plot_div = plot_div.replace(
        '<tr style="text-align: right;">',
        '<tr style="text-align: left;">')
    plots.append({"title": title, "div": plot_div})
    d_buttons.append({"text": "Download Raw data", "target": "table_div"})

    return render_template('results.html', plots=plots, html_scripts=html_scripts, d_buttons=d_buttons)


@app.route('/', methods=['GET', 'POST'])
def home():
    df = get_names()
    name_pairs = list(df[['pandel_id', 'pandel_name']].itertuples(index=False, name=None))
    name_pairs = [('--all--', '--all--')] + name_pairs


    class PandelAnalysisForm(FlaskForm):
        pandel_choice = SelectField(
            label='pandel_choice',
            choices=name_pairs
        )

    form = PandelAnalysisForm()

    if form.validate_on_submit():
        pandel_id = str(form.pandel_choice.data).strip()
        return results_on(pandel_id)

    return render_template('home.html', form=form)



@app.route('/ok', methods=['GET', 'POST'])
def ok():
    return "ok", 200


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=True)